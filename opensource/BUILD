# Description:
#    Open-sourcing build rules for the
#    https://github.com/googleads/googleads-mobile-android-mediation GitHub repo.

package(default_visibility = ["//visibility:public"])

load("/devtools/moe/builddefs/builddefs", "GenInternalCodebase", "GenMoeCodebase")

GenInternalCodebase(
    name = "mediation",
    srcs = [
        "//java/com/google/android/libraries/admob/demo/mediation_adapter:mediation_files",
        # Used for moe support only.
        "//third_party/moe_support/DO_NOT_USE/libraries/gradle_wrapper:opensource_files",
        "LICENSE",
        "README.md",
    ],
    out = "mediation.tar",
)

GenMoeCodebase(
    name = "mediation_public",
    config_file = "moe_config.txt",
    from_project_space = "internal",
    gen_internal_codebase = ":mediation",
    out_tarfile = "mediation_public.tar",
    to_project_space = "public",
)
