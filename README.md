# Custom Lint Rules

## Current rules

* [Method should not declare more than 5 parameters](https://medium.com/@guilhermekrz/how-to-implement-your-first-custom-lint-rule-in-android-using-tdd-part-1-d3c9a58a7aa8)
* [Should only construct OkHttpClient once](https://medium.com/@guilhermekrz/how-to-implement-a-custom-lint-rule-in-android-that-requires-an-overall-view-of-the-project-part-34f1371cf0c3)
* [Do not throw Exception from Kotlin code (either by annotating the method as @Throws or by actually throwing a new Exception)](https://medium.com/@guilhermekrz/how-to-implement-a-custom-lint-rule-in-android-to-warn-against-checked-exception-thrown-from-a076eb9fecd5)
* Detect if Java code throws and Kotlin code does not catch
* Detect if you are setting the fragment manager before calling Activity onCreate method

## Ideas

* Detect if we are using immutable list/set/map and Java needs a mutable list/set/map

## Resources

* Android Official Lint Doc: https://developer.android.com/studio/write/lint
* Android Official Sample: https://github.com/googlesamples/android-custom-lint-rules
* All of Android lint checks are available at https://android.googlesource.com/platform/tools/base/+/master/lint/libs/lint-checks/src/main/java/com/android/tools/lint/checks
* Initial code based on https://github.com/fabiocarballo/lint-sample
* Other custom lint rules:
    * https://www.bignerdranch.com/blog/building-custom-lint-checks-in-android/
    * https://jayrambhia.com/blog/android-lint and https://jayrambhia.com/blog/android-lint-ref
    * https://proandroiddev.com/enforcing-clean-architecture-using-android-custom-lint-rules-aa8fc1708c59
    * https://medium.com/supercharges-mobile-product-guide/formatting-code-analysis-rule-with-android-lint-part-1-2-4b906f717382
    * https://medium.com/@sinankozak/android-lint-rule-for-immutable-kotlin-data-classes-5c91517c7611
