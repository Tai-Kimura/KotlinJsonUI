# Consumer ProGuard rules for KotlinJsonUI library
#
# Deliberately no -assumenosideeffects / -assumevalues here
# (ticket kjui-consumer-rules-assumenosideeffects-strips-dashed-border-draw).
#
# Up to 2.43.6 this file declared
#   -assumenosideeffects class com.kotlinjsonui.dynamic.** { public/private/protected <methods>; }
# meaning to "remove the dynamic package in release builds". It never did that:
# -assumenosideeffects removes calls whose results are unused, it does not
# remove classes. kotlinjsonui-dynamic stays out of release because apps
# depend on it with debugImplementation, and R8 only runs on release, where
# none of its classes exist. What the rule did match in release were the
# classes this module keeps in the com.kotlinjsonui.dynamic package for
# source compatibility (helpers.DashedBorderModifierKt, SafeAreaConfig,
# SafeAreaConfigKt): R8 emptied the dashedBorder / dottedBorder draw lambda,
# so drawContent() and the border were both gone. The wildcard <methods>
# also matched java.lang.Object's methods.
#
# library/src/test/.../ConsumerRulesTest.kt keeps every file named in
# consumerProguardFiles free of these rules.
