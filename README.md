# espresso - ZIRO's Java Utility Library

espresso is a library containing utilities meant to support all other java projects at ZIRO.

### Development:
Create your feature branch and start working. To test how the changes you are making will work with another project, run:
```
./gradlew clean build
./gradlew publish
```
This will publish a snapshot of the library to our internal Maven repository with the name `<version>-SNAPSHOT`

You can then go to the other project, and change the version that project consumes to match your snapshot.

### Testing:
Run the unit tests with:
`./gradlew test`

### Publishing Your Changes
Other projects ***SHOULD NOT*** be consuming snapshots (aside from locally during development). Instead, they should only consume
proper releases. Publishing and tagging releases is handled by Jenkins. When you merge your changes to `main`, it triggers a Jenkins
job which builds and tests the repo, then attempts to tag the appropriate commit and publish a release. This versioning is managed by
`./version.txt`. YOU MUST BUMP THE VERSION WHEN MAKING CODE CHANGES. Please follow [semantic versioning](https://semver.org/)

### Release Notes
