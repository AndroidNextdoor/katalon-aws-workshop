# Katalon AWS Workshop
### PRE-REQUISITES
This is a repo used to demonstrate integrations between Katalon and AWS. It accompanies an AWS Kickstarter [here]()

### Update your aws credentials file with profiles outline in the AWS-Kickstarter

### Set ENV Variables
- `REGION=<YOUR DEFAULT REGION>`
- `AWS_ACCOUNT_ID=<YOUR AWS ACCOUNT>`
- `KATALON_API_KEY=<YOUR KATALON API KEY>` 
- `MYSQL_DB_U=<YOUR_MYSQL_USERNAME>`
- `MYSQL_DB_P=<YOUR_MYSQL_PASSWORD>`

If you need help setting an environment variable, please refer [to this article](https://www.twilio.com/blog/2017/01/how-to-set-environment-variables.html) for help.

## READ THIS BEFORE OPENING A KATALON PROJECT FOR THE FIRST TIME
Katalon-Projects use gradle tasks to perform setup tasks i.e. getting dependencies
To get started, use IntelliJ IDEA to synchronize the gradle tasks and run the `katalonCopyDependencies` tasks in the project you are opening for the first time.

Alternatively you can run the following gradle commands in the project you would like to configure.
  - `./gradlew :$PROJECT:katalonCopyDependencies`

Once start-up files have been initialized, you can open the `$PROJECT` in Katalon.

Many of the scripts in the `_cli` do not actually work without configuration of ENV variables in AWS SecretsManager. ENV variables are set by retrieving secrets from AWS.

### Demo Docker Configuration
The `Dockerfile` and `docker-compose.yml` files contain sample configuration that can be used to network a katalon docker instance with a mysql database.
Sample docker scripts can be found in `_cli/docker` 

### Keywords.jar plugin
To update the keywords jar for all projects:
1. Ensure that all projects which require the Keywords.jar plugin are listed within the `updatePlugins` task in
   build.gradle for :Keywords (Keywords/build.gradle)
2. Run the `clean` gradle task (Tasks>build>clean)
3. Run the Keywords specific `katalonPluginPackage` gradle task (Keywords>Tasks>other>katalonPluginPackage) - this step takes a bit of time (can take 5+ minutes), wait for it to finish
4. Run the Keywords specific `updatePlugins` gradle task (Keywords>Tasks>other>updatePlugins)
5. Run the Keywords specific `updateEnvManagers` gradle task (Keywords>Tasks>other>updateEnvManagers) - only necessary if EnvManager has been updated
6. After running this, you will need to run `katalonCopyDependencies` gradle task (Project:Tasks>other>katalonCopyDependencies) again before opening a project in Katalon. If this step is forgotten, errors will be present when opening the project in Katalon.

