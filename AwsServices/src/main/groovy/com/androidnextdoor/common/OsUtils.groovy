package com.androidnextdoor.common

import org.apache.commons.lang3.SystemUtils

import java.nio.file.Path
import java.nio.file.Paths
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class OsUtils {
	private static Logger logger = LoggerFactory.getLogger(OsUtils.class);

	static String runCommand(
			String command,
			Path workingDirectory,
			Map<String, String> environmentVariablesMap)
	throws IOException, InterruptedException {

		//Only allows aws cli commands
		if(command.startsWith("aws")) {
			String[] cmdArray = SystemUtils.IS_OS_WINDOWS ? ["cmd.exe", "/c", command] : ['bash', '-c', command]

			if (workingDirectory == null) {
				workingDirectory = Paths.get('.').toAbsolutePath()
			}

			if (environmentVariablesMap == null) {
				environmentVariablesMap = Collections.emptyMap()
			}

			logger.debug("Executing command: ${Arrays.toString(cmdArray)} in ${workingDirectory.toAbsolutePath()}")

			ProcessBuilder pb = new ProcessBuilder(cmdArray)
			Map<String, String> env = pb.environment()
			if (environmentVariablesMap != null) {
				env.putAll(environmentVariablesMap)
			}
			pb.directory(workingDirectory.toFile())
			pb.redirectErrorStream(true)
			Process cmdProc = pb.start()

			BufferedReader br = new BufferedReader(new InputStreamReader(cmdProc.getInputStream()));

			String line = "";
			String response = "";
			while ((line = br.readLine()) != null) {
				response = response += line
			}

			logger.debug("Command Response: " + response)

			return response
		} else {
			return "Command NOT Allowed!!!"
		}
	}
}
