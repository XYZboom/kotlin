// RUN_PIPELINE_TILL: FRONTEND

interface Foo

@Target(AnnotationTarget.TYPE)
annotation class Ann

class E : <!ANNOTATION_ON_SUPERCLASS_ERROR, WRONG_ANNOTATION_TARGET_WITH_USE_SITE_TARGET!>@field:Ann<!> <!ANNOTATION_ON_SUPERCLASS_ERROR, WRONG_ANNOTATION_TARGET_WITH_USE_SITE_TARGET!>@get:Ann<!> <!ANNOTATION_ON_SUPERCLASS_ERROR, WRONG_ANNOTATION_TARGET_WITH_USE_SITE_TARGET!>@set:Ann<!> <!ANNOTATION_ON_SUPERCLASS_ERROR, WRONG_ANNOTATION_TARGET_WITH_USE_SITE_TARGET!>@setparam:Ann<!> Foo

interface G : @Ann Foo

/* GENERATED_FIR_TAGS: annotationDeclaration, annotationUseSiteTargetField, annotationUseSiteTargetPropertyGetter,
annotationUseSiteTargetPropertySetter, annotationUseSiteTargetSetterParameter, classDeclaration, interfaceDeclaration */
