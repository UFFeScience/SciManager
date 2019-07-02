package com.uff.workflow.invoker.strategy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.uff.workflow.invoker.domain.WorkflowExecution;
import com.uff.workflow.invoker.exception.WorkflowExecutionException;
import com.uff.workflow.invoker.util.CalendarDateUtils;
import com.uff.workflow.invoker.util.FileUtils;

@Component("sciCumulusInvokerStrategy")
public class SciCumulusInvokerStrategy implements WorkflowSystemInvokerStrategy {
	
	private static final Logger log = LoggerFactory.getLogger(SciCumulusInvokerStrategy.class);
	
	public static final String SC_SETUP_JAR = "SCSetup.jar";
	public static final String SC_STARTER_JAR = "SCStarter.jar";
	
	private String workflowSystemPath;
	
	@Value("${scicumulus.path}")
    public void setWorkflowSystemPath(String workflowSystemPath) {
		this.workflowSystemPath = System.getProperty("user.home") + "/" + workflowSystemPath;
    }
	
	@Override
	public void invokeWorkflowSystem(WorkflowExecution workflowExecution) throws IOException, WorkflowExecutionException, InterruptedException {
		String filePath = buildModelFilePathName(workflowExecution);
		String fileName = buildModelFileName(workflowExecution);
		
		FileUtils.createFileFromBytes(filePath, fileName, workflowExecution.getModelFile().getFileContent());
		
		executeSetUp(filePath + fileName);
		executeStarter(filePath + fileName);
	}
	
	private String buildModelFilePathName(WorkflowExecution workflowExecution) {
		return workflowSystemPath + "workflowModelFiles/" + workflowExecution.getWorkflowExecutionId() + "/" + 
			   workflowExecution.getExecTag() + "/" + CalendarDateUtils.formatDate(workflowExecution.getExecutionDate()) + "/";
	}
	
	private String buildModelFileName(WorkflowExecution workflowExecution) {
		return "model-file-" + workflowExecution.getWorkflowExecutionId() + "-" + workflowExecution.getExecTag() + ".xml";
	}
	
	private void executeSetUp(String filePath) throws IOException, WorkflowExecutionException, InterruptedException {
		log.info("Iniciando processo de " + SC_SETUP_JAR + " para workflow com arquivo '{}'", filePath);
		
		StringBuilder commandLine = new StringBuilder("java -jar ").append(workflowSystemPath)
															       .append(SC_SETUP_JAR)
															       .append(" -i ")
															       .append(filePath);
		
		Process process = Runtime.getRuntime().exec(commandLine.toString());
		process.waitFor();
		
		if (process.exitValue() != 0) {
			logProcessError(SC_SETUP_JAR, process);
        }
		else {
			logProcessExecution(SC_SETUP_JAR, process);
		}
	}

	private void executeStarter(String filePath) throws IOException, WorkflowExecutionException, InterruptedException {
		log.info("Iniciando processo de " + SC_STARTER_JAR + " para workflow com arquivo '{}'", filePath);
		
		StringBuilder commandLine = new StringBuilder("java -jar ").append(workflowSystemPath)
															       .append(SC_STARTER_JAR)
															       .append(" -swe ")
															       .append(filePath);
		
        Process process = Runtime.getRuntime().exec(commandLine.toString());
        process.waitFor();
        
        if (process.exitValue() != 0) {
        	logProcessError(SC_STARTER_JAR, process);
        }
        else {
        	logProcessExecution(SC_STARTER_JAR, process);
        }
	}
	
	private void logProcessExecution(String jarName, Process process) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null;
        
        log.info("Logs do processo " + jarName + ":");
        
        while ((line = bufferedReader.readLine()) != null) {
            log.info(line);
        }

        process.destroy();
        log.info("Processo " + jarName + " finalizado.");
	}

	private void logProcessError(String jarName, Process process) throws IOException, WorkflowExecutionException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		StringBuilder errorLog = new StringBuilder();
		String line = null;
		
		log.info("Logs de erro do processo " + jarName + ":");
		
		while ((line = bufferedReader.readLine()) != null) {
		    log.info(line);
		    errorLog.append(line);
		    errorLog.append("\n");
		}
		
		log.error("Processo " + jarName + " finalizado com erro.");
		
		process.destroy();
		throw new WorkflowExecutionException("Erro executando " + jarName + ", processo terminou com código de erro", errorLog.toString());
	}

}