By default these projects are not builded unless an `android` profile is activated in maven.

To build this set of android projects, it is needed to:
* set the android path
* activate the `android` profile

For example, in the maven settings file `settings.xml`

```xml
  <profiles>
    <profile>
      <id>android</id>
      <properties>
        <android.sdk.path>
          /opt/development/android-sdk-linux
        </android.sdk.path>
      </properties>
    </profile>
  </profiles>

  <activeProfiles>
    <activeProfile>android</activeProfile>
  </activeProfiles>
```
