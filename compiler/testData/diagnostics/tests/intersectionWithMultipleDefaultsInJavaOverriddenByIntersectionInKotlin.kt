// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// FILE: ColoredTextContainer.java
public interface ColoredTextContainer {
    default void setToolTipText(String text) {}
}

// FILE: JComponent.java
public abstract class JComponent {
    public void setToolTipText(String text) {}
}

// FILE: SimpleColoredComponent.java
// IDEALLY:
//     Provides `IO SimpleColoredComponent::setToolTipText`, because inherits
//     `JComponent::setToolTipText` and `ColoredTextContainer::setToolTipText`,
//     which don't sumbsume one another, but Java allows picking the class-based one in this case.
// REALITY:
//     Contains an IO for the above. We check if this is green Java via a
//     modified check that accounts for this case.
public class SimpleColoredComponent extends JComponent implements ColoredTextContainer {}

// FILE: Main.kt

// Not important, left for the record
interface TextFragment : ColoredTextContainer

// IDEALLY:
//     Provides `JComponent::setToolTipText`, because inherits
//     `ColoredTextContainer::setToolTipText` and `IO SimpleColoredComponent::setToolTipText`,
//     and the latter subsumes the former.
// REALITY:
//     Contains IO between `ColoredTextContainer::setToolTipText` and `IO SimpleColoredComponent::setToolTipText`.
//     In this case `IO SimpleColoredComponent::setToolTipText` should not be unwrapped, otherwise
//     we miss `nonSubsumed()` check and since this is a Kotlin class we are not allowed to implicitly choose
//     between `JComponent::setToolTipText` and `ColoredTextContainer::setToolTipText`.
private class TextFragmentImpl : TextFragment, SimpleColoredComponent()

/* GENERATED_FIR_TAGS: classDeclaration, interfaceDeclaration, javaType */
