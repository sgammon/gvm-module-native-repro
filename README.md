# graalvm bug repro

**Summary:** Building a Java entrypoint module and enabling that module for native access with `--enable-native-access` should work, but instead fails because the module "cannot be found."

