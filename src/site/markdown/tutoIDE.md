# Tutorial: Light Follow Me

// Introduction


## 1. Prerequisite




## 2. Application Development

### 2.1. Project Creation and Skeleton Generation

You need to create and generate the skeleton of the unique class of your application.

1. Create a new iPOJO Project.

2. Configure the iPOJO Preferences. Go to *Windows -> Preferences -> iPOJO Preferences.*

3. Create a new component *BinaryFollowMe*. Open the `metadata.xml` file with the iPOJO Metadata Editor.

4. Click *Add* and change the component name to *BinaryFollowMe*.

5. Add to service dependencies (i.e. required services) with **Multiple** and **Optional** characteristics:

   * one dependency to `BinaryLight` with a field `binaryLights` and bind/unbind methods named respectively `bindBinaryLight` and `unbindBinaryLight`;
   * one dependency to `PresenceSensor` with a field `presenceSensors` and bind/unbind methods named respectively `bindPresenceSensor` and `unbindPresenceSensor`.


## 3. Application Deployment and Test
