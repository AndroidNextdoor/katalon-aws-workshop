# Katalon AwsKeywords
To learn more about katalon keywords please see katalon documentation:
https://docs.katalon.com/katalon-studio/docs/introduction-to-custom-keywords.html

https://docs.katalon.com/katalon-store/docs/publisher/build-CK-settings.html#add-settings-page

### Building the AwsKeywords.jar Plugin
# Include the 
1. Ensure that all projects which require the Keywords.jar plugin are listed within the `updatePlugins` task in
   build.gradle for :AwsKeywords (Keywords/build.gradle)
2. Run the `clean` gradle task (Tasks>build>clean)
3. Run the Keywords specific `katalonPluginPackage` gradle task (Keywords>Tasks>other>katalonPluginPackage)
4. Run the Keywords specific `updatePlugins` gradle task (Keywords>Tasks>other>updatePlugins)
6. After running this, you will need to run `katalonCopyDependencies` again before opening a project in Katalon. If this step is forgotten, errors will be present when opening the project in Katalon.