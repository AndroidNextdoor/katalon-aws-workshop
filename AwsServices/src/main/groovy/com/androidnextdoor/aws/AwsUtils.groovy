package com.androidnextdoor.aws

import groovy.json.JsonSlurper
import org.apache.commons.lang3.StringUtils
import com.androidnextdoor.common.OsUtils
import software.amazon.awssdk.auth.credentials.AwsCredentialsProviderChain
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider

import java.nio.file.Paths
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

public class AwsUtils {

	private static JsonSlurper jsonSlurper = new JsonSlurper()
	private static OsUtils osUtils = new OsUtils()
	private static Logger logger = LoggerFactory.getLogger(AwsUtils.class);

	// -----------------------------------------
	static String getBearerToken(String awsProfile, String jsonAuth){
		def pwd = Paths.get('.').toAbsolutePath().toString()
		pwd = StringUtils.chop(pwd)

		Map<String, String> envVariables = new HashMap<>()
		String PATH = System.getenv('PATH')
		PATH = "/usr/local/bin:${PATH}"
		envVariables.put("PATH", PATH)

		String command = "aws cognito-idp admin-initiate-auth --profile ${awsProfile} --region us-west-2 --cli-input-json file://${pwd}Include/resources/auth/${jsonAuth}"

		logger.debug("Cognito Command: " + command)

		def response = osUtils.runCommand(command, null, envVariables);
		def jsonResponse = jsonSlurper.parseText(response)
		def bearerToken = jsonResponse.AuthenticationResult.AccessToken.toString()

		logger.debug("BearerToken: "+bearerToken)

		return bearerToken
	}

	// Katalon GlobalVariables need to be passed into these functions
	// -----------------------------------------
	static String getGridUrl(String awsProfile, String deviceFarmARN, String maxTimeoutSecs){
		def pwd = Paths.get('.').toAbsolutePath().toString()
		pwd = StringUtils.chop(pwd)

		Map<String, String> envVariables = new HashMap<>()
		String PATH = System.getenv('PATH')
		PATH = "/usr/local/bin:${PATH}"
		envVariables.put("PATH", PATH)

		String command = "aws devicefarm create-test-grid-url --profile ${awsProfile} --project-arn '${deviceFarmARN}' --expires-in-seconds ${maxTimeoutSecs}"

		def response = osUtils.runCommand(command, null, envVariables);

		def jsonResponse = jsonSlurper.parseText(response)
		def gridUrl = jsonResponse.url.toString()

		logger.debug("DeviceFarm Grid Url: " + gridUrl)

		return gridUrl
	}

	public String retrieveSecret(String awsProfile, String region, String secretName, String secretKey){
		def pwd = Paths.get('.').toAbsolutePath().toString()
		pwd = StringUtils.chop(pwd)

		Map<String, String> envVariables = new HashMap<>()
		String PATH = System.getenv('PATH')
		PATH = "/usr/local/bin:${PATH}"
		envVariables.put("PATH", PATH)

		String command = "aws secretsmanager get-secret-value --region ${region} --profile ${awsProfile} --secret-id ${secretName}"

		def response = osUtils.runCommand(command, null, envVariables);

		def jsonResponse = jsonSlurper.parseText(response)
		def secretStringObject = jsonResponse.SecretString
		def secretString = jsonSlurper.parseText(secretStringObject)
		def value = secretString."${secretKey}"

		logger.debug(value)

		return value
	}
}
