declare namespace JS_TESTS {
    type Nullable<T> = T | null | undefined
    function KtSingleton<T>(): T & (abstract new() => any);
    namespace foo {
        interface OptionalFieldsInterface {
            readonly required: number;
            readonly notRequired?: Nullable<number>;
        }
        interface ExportedParentInterface {
        }
    }
    namespace foo {
        interface TestInterface {
            readonly value: string;
            getOwnerName(): string;
            readonly __doNotUseOrImplementIt: {
                readonly "foo.TestInterface": unique symbol;
            };
        }
        interface AnotherExportedInterface {
            readonly __doNotUseOrImplementIt: {
                readonly "foo.AnotherExportedInterface": unique symbol;
            };
        }
        class TestInterfaceImpl implements foo.TestInterface {
            constructor(value: string);
            get value(): string;
            getOwnerName(): string;
            readonly __doNotUseOrImplementIt: foo.TestInterface["__doNotUseOrImplementIt"];
        }
        /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
        namespace TestInterfaceImpl.$metadata$ {
            const constructor: abstract new () => TestInterfaceImpl;
        }
        class ChildTestInterfaceImpl extends foo.TestInterfaceImpl.$metadata$.constructor implements foo.AnotherExportedInterface {
            constructor();
            readonly __doNotUseOrImplementIt: foo.TestInterfaceImpl["__doNotUseOrImplementIt"] & foo.AnotherExportedInterface["__doNotUseOrImplementIt"];
        }
        /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
        namespace ChildTestInterfaceImpl.$metadata$ {
            const constructor: abstract new () => ChildTestInterfaceImpl;
        }
        function processInterface(test: foo.TestInterface): string;
        interface WithTheCompanion {
            readonly interfaceField: string;
            readonly __doNotUseOrImplementIt: {
                readonly "foo.WithTheCompanion": unique symbol;
            };
        }
        abstract class WithTheCompanion extends KtSingleton<WithTheCompanion.$metadata$.constructor>() {
            private constructor();
        }
        /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
        namespace WithTheCompanion.$metadata$ {
            abstract class constructor {
                companionFunction(): string;
                private constructor();
            }
        }
        function processOptionalInterface(a: foo.OptionalFieldsInterface): string;
        interface InterfaceWithCompanion {
            readonly __doNotUseOrImplementIt: {
                readonly "foo.InterfaceWithCompanion": unique symbol;
            };
        }
        interface ExportedChildInterface extends foo.ExportedParentInterface {
            bar(): void;
            readonly __doNotUseOrImplementIt: {
                readonly "foo.ExportedChildInterface": unique symbol;
            };
        }
        interface InterfaceWithDefaultArguments {
            foo(x?: number): number;
            bar(x?: number): number;
            readonly __doNotUseOrImplementIt: {
                readonly "foo.InterfaceWithDefaultArguments": unique symbol;
            };
        }
        class ImplementorOfInterfaceWithDefaultArguments implements foo.InterfaceWithDefaultArguments {
            constructor();
            bar(x?: number): number;
            foo(x?: number): number;
            readonly __doNotUseOrImplementIt: foo.InterfaceWithDefaultArguments["__doNotUseOrImplementIt"];
        }
        /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
        namespace ImplementorOfInterfaceWithDefaultArguments.$metadata$ {
            const constructor: abstract new () => ImplementorOfInterfaceWithDefaultArguments;
        }
    }
}
