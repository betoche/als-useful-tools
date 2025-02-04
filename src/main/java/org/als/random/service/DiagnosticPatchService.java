package org.als.random.service;

import org.als.teamconnect.entity.DiagnosticPatchInfo;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DiagnosticPatchService {

    public String generateSuggestedInstructions(String ticketNumber, String tceNumber, String branchName, List<DiagnosticPatchInfo.FoundClass> classFileList) {
        List<String> instructions = new ArrayList<>();

        instructions.add("Diagnostic Patch Instructions");
        instructions.add("");
        instructions.add(String.format("Ticket #: %s", ticketNumber));
        instructions.add(String.format("TCE version: %s", tceNumber));
        instructions.add(String.format("Branch: %s", branchName));
        instructions.add("");
        instructions.add("- First Backup the following class files:");
        for( DiagnosticPatchInfo.FoundClass filePath : classFileList ) {
            instructions.add(String.format("    - %s%s\\%s", filePath.getJarLibPath(), filePath.getJarFileSimpleName(), filePath.getPackagePath()));
        }
        instructions.add("- Second copy the classes contained in the zip file in their respectives directories:");
        for( DiagnosticPatchInfo.FoundClass filePath : classFileList ){
            instructions.add(String.format("    - %s -> %s%s\\%s", filePath.getClassName(), filePath.getJarLibPath(), filePath.getJarFileSimpleName(), filePath.getPackagePath()));
        }

        return Strings.join(instructions, '\n');
    }
}
