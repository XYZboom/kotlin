/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 *
 * ASM: a very small and fast Java bytecode manipulation framework
 * Copyright (c) 2000-2011 INRIA, France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.jetbrains.kotlin.codegen.optimization.fixStack

import org.jetbrains.kotlin.codegen.optimization.common.FastAnalyzer
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.tree.*
import org.jetbrains.org.objectweb.asm.tree.analysis.Frame
import org.jetbrains.org.objectweb.asm.tree.analysis.Interpreter
import org.jetbrains.org.objectweb.asm.tree.analysis.Value

/**
 * @see org.jetbrains.kotlin.codegen.optimization.common.FastMethodAnalyzer
 */
internal open class FastStackAnalyzer<V : Value>(
    owner: String,
    method: MethodNode,
    interpreter: Interpreter<V>
) : FastAnalyzer<V, Interpreter<V>, Frame<V>>(owner, method, interpreter) {
    override fun newFrame(nLocals: Int, nStack: Int): Frame<V> = Frame(nLocals, nStack)

    protected open fun visitControlFlowEdge(insnNode: AbstractInsnNode, successor: Int): Boolean = true

    protected open fun visitControlFlowExceptionEdge(insn: Int, successor: Int): Boolean = true

    fun analyze(): Array<Frame<V>?> {
        if (nInsns == 0) return getFrames()

        // This is a very specific version of method bytecode analyzer that doesn't perform any DFA,
        // but infers stack types for reachable instructions instead.

        checkAssertions()

        // Don't have to visit same exception handler multiple times - we care only about stack state at TCB start.
        computeExceptionHandlers(method, forEachInsn = false)

        analyzeInner()

        return getFrames()
    }

    override fun privateAnalyze(
        insnNode: AbstractInsnNode,
        insnIndex: Int,
        insnType: Int,
        insnOpcode: Int,
        currentlyAnalyzing: Frame<V>,
        current: Frame<V>,
        handler: Frame<V>,
    ) {
        if (insnType == AbstractInsnNode.LABEL || insnType == AbstractInsnNode.LINE || insnType == AbstractInsnNode.FRAME) {
            visitNopInsn(insnNode, currentlyAnalyzing, insnIndex)
        } else {
            current.init(currentlyAnalyzing)
            if (insnOpcode != Opcodes.RETURN) {
                // Don't care about possibly incompatible return type
                current.execute(insnNode, interpreter)
            }
            visitMeaningfulInstruction(insnNode, insnType, insnOpcode, current, insnIndex)
        }

        handlers[insnIndex]?.forEach { tcb ->
            val exnType = Type.getObjectType(tcb.type ?: "java/lang/Throwable")
            val jump = tcb.handler.indexOf()
            if (visitControlFlowExceptionEdge(insnIndex, jump)) {
                handler.init(currentlyAnalyzing)
                handler.clearStack()
                handler.push(interpreter.newValue(exnType))
                mergeControlFlowEdge(jump, handler)
            }
        }
    }

    override fun visitOpInsn(insnNode: AbstractInsnNode, current: Frame<V>, insn: Int) {
        processControlFlowEdge(current, insnNode, insn + 1)
    }

    private fun visitNopInsn(insnNode: AbstractInsnNode, f: Frame<V>, insn: Int) {
        processControlFlowEdge(f, insnNode, insn + 1)
    }

    override fun visitTableSwitchInsnNode(insnNode: TableSwitchInsnNode, current: Frame<V>) {
        var jump = insnNode.dflt.indexOf()
        processControlFlowEdge(current, insnNode, jump)
        // In most cases order of visiting switch labels should not matter
        // The only one is a tableswitch being added in the beginning of coroutine method, these switch' labels may lead
        // in the middle of try/catch block, and FixStackAnalyzer is not ready for this (trying to restore stack before it was saved)
        // So we just fix the order of labels being traversed: the first one should be one at the method beginning
        // Using 'reversed' is because nodes are processed in LIFO order
        for (label in insnNode.labels.reversed()) {
            jump = label.indexOf()
            processControlFlowEdge(current, insnNode, jump)
        }
    }

    override fun visitLookupSwitchInsnNode(insnNode: LookupSwitchInsnNode, current: Frame<V>) {
        var jump = insnNode.dflt.indexOf()
        processControlFlowEdge(current, insnNode, jump)
        for (label in insnNode.labels) {
            jump = label.indexOf()
            processControlFlowEdge(current, insnNode, jump)
        }
    }

    override fun visitJumpInsnNode(insnNode: JumpInsnNode, current: Frame<V>, insn: Int, insnOpcode: Int) {
        if (insnOpcode != Opcodes.GOTO) {
            processControlFlowEdge(current, insnNode, insn + 1)
        }
        val jump = insnNode.label.indexOf()
        processControlFlowEdge(current, insnNode, jump)
    }

    private fun processControlFlowEdge(current: Frame<V>, insnNode: AbstractInsnNode, jump: Int) {
        if (visitControlFlowEdge(insnNode, jump)) {
            mergeControlFlowEdge(jump, current)
        }
    }

    override fun initLocals(current: Frame<V>) {
        current.setReturn(interpreter.newValue(Type.getReturnType(method.desc)))
        val args = Type.getArgumentTypes(method.desc)
        var local = 0
        val isInstanceMethod = (method.access and Opcodes.ACC_STATIC) == 0
        if (isInstanceMethod) {
            val ctype = Type.getObjectType(owner)
            current.setLocal(local, interpreter.newValue(ctype))
            local++
        }
        for (arg in args) {
            current.setLocal(local, interpreter.newValue(arg))
            local++
            if (arg.size == 2) {
                current.setLocal(local, interpreter.newValue(null))
                local++
            }
        }
        while (local < method.maxLocals) {
            current.setLocal(local, interpreter.newValue(null))
            local++
        }
    }

    override fun mergeControlFlowEdge(dest: Int, frame: Frame<V>, canReuse: Boolean) {
        val destFrame = getFrame(dest)
        if (destFrame == null) {
            // Don't have to visit same instruction multiple times - we care only about "initial" stack state.
            setFrame(dest, newFrame(frame.locals, frame.maxStackSize).apply { init(frame) })
            updateQueue(true, dest)
        }
    }

}
