package org.als.teamconnect.controller;

import org.als.random.utils.MavenProjectFileSystemFinder;
import org.als.teamconnect.entity.DiagnosticPatchInfo;
import org.als.teamconnect.entity.DownloadDiagnosticPatchRequest;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

@Controller
@RequestMapping("/diagnostic-patch")
public class DiagnosticPatchPackerController {
    private final static Logger LOGGER = LoggerFactory.getLogger(DiagnosticPatchPackerController.class);

    @GetMapping({"", "/", "/process"})
    public ModelAndView getDiagnosticPatchHome(){
        ModelAndView mv = new ModelAndView("diagnostic-patch");
        mv.addObject("ticketNumber", "SOH-4877");
        mv.addObject("tceNumber", "6.3.6");
        mv.addObject("projectDir", "C:\\Users\\betoc\\repositories\\TCE6.3.6\\teamconnectenterprise");
        mv.addObject("jarFilesDir", "C:\\Users\\betoc\\repositories\\TCE6.3.6\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\teamconnect-webui\\WEB-INF\\lib");
        mv.addObject("branchName", "bugfix/SOH-4877-diagnostic-patch");
        mv.addObject("addFile", Arrays.asList("BQDetailFieldDynamicValue,CaptureDesignChanges,DLTable".split(",")));

        return mv;
    }

    @PostMapping("/download-zip-file")
    public ResponseEntity<?> downloadDiagnosticPatchZipFile(@RequestBody DownloadDiagnosticPatchRequest request ) throws IOException {
        File outputDir = new File("output");
        if( !outputDir.exists() ){
            outputDir = Files.createDirectory(Path.of("output")).toFile();
        }

        String zipDirPathStr = String.format("%s\\%s", outputDir.getAbsolutePath(), request.getZipFileName().replace(".zip",""));
        File tmpZipDirectory = new File(zipDirPathStr);
        if( !tmpZipDirectory.exists() ) {
            Files.createDirectory(Path.of(zipDirPathStr));
        }

        File patchInstructionsFile = new File(String.format("%s\\%s", zipDirPathStr,"patch-instructions.txt"));
        Path path = Paths.get(patchInstructionsFile.getAbsolutePath());
        byte[] strToBytes = request.getPatchInstructions().getBytes();
        Files.write(path, strToBytes);

        for( String filePath : request.getClassFileList() ) {
            File tmpFile = new File(filePath);
            Files.copy(Path.of(filePath), Path.of(String.format("%s\\%s", zipDirPathStr,tmpFile.getName())), StandardCopyOption.REPLACE_EXISTING);
        }

        return ResponseEntity.ok("empty");
    }

    @PostMapping("/process")
    public ModelAndView getProcessDiagnosticPatch(@RequestParam("ticket-number") String ticketNumber,
                                                  @RequestParam("tce-number") String tceNumber,
                                                  @RequestParam("project-dir") String projectDirectory,
                                                  @RequestParam("branch-name") String branchName,
                                                  @RequestParam("add-file") List<String> fileList,
                                                  @RequestParam("jar-files-directory") String jarFilesDirectory)
            throws IOException, InterruptedException {
        boolean isValidRequest = true;
        File projDirFile = new File(projectDirectory);
        List<String> errorList = new ArrayList<>();

        if( !projDirFile.exists() || !projDirFile.isDirectory() ) {
            errorList.add("Invalid project directory.");
            isValidRequest = false;
        }
        String currentBranch = getCurrentGitBranch(projDirFile);
        if( currentBranch==null || !currentBranch.equalsIgnoreCase(branchName) ) {
            errorList.add("The project is in a different branch name.");
            isValidRequest = false;
        }
        if( ticketNumber.isEmpty() ) {
            errorList.add("The ticket name is empty.");
            isValidRequest = false;
        }
        if( tceNumber.isEmpty() ) {
            errorList.add("The TeamConnect version is empty.");
            isValidRequest = false;
        }
        MavenProjectFileSystemFinder mvnProjFileSys = new MavenProjectFileSystemFinder();
        List<String> classFileList = mvnProjFileSys.getFilePathListInTarget(projectDirectory );
        boolean containsClass = false;
        for( String className : fileList ) {
            containsClass = false;
            for( String classAbsolutePath : classFileList ) {
                if (classAbsolutePath.contains(className)) {
                    containsClass = true;
                    break;
                }
            }
            if( !containsClass ){
                errorList.add(String.format("The class \"%s\" was not found in the project.", className));
                isValidRequest = false;
            }
        }

        LOGGER.info(String.format("{ ticketNumber: %s, tceNumber: %s, projectDirectory: %s, branchName: %s, fileList: %s }", ticketNumber, tceNumber, projectDirectory, branchName, fileList));

        ModelAndView mv = new ModelAndView("diagnostic-patch");
        if( isValidRequest ){
            mv.addObject("patchInfo", processDiagnosticPatchInfo( jarFilesDirectory, ticketNumber, tceNumber, branchName, fileList, classFileList));
        }

        mv.addObject("ticketNumber", ticketNumber);
        mv.addObject("tceNumber", tceNumber);
        mv.addObject("projectDir", projectDirectory);
        mv.addObject("jarFilesDir", jarFilesDirectory);
        mv.addObject("branchName", branchName);
        mv.addObject("addFile", fileList);
        mv.addObject("errorList", errorList);
        return mv;
    }

    private DiagnosticPatchInfo processDiagnosticPatchInfo(String directory, String ticketNumber, String tceNumber,
                                                           String branchName, List<String> fileList, List<String> classFileList) throws IOException {
        List<String> foundClasses = new ArrayList<>();
        for( String className : fileList ) {
            foundClasses.addAll(classFileList.stream().filter(s -> s.contains(className)).toList());
        }

        List<DiagnosticPatchInfo.FoundClass> foundClassList = new ArrayList<>();
        MavenProjectFileSystemFinder mvnProjFileSys = new MavenProjectFileSystemFinder();
        List<String> jarList = mvnProjFileSys.getFilePathList(directory, new String[]{".jar"}, null);
        for( String className : foundClasses ) {
            String jarName = getJarNameForClass(className, jarList);
            foundClassList.add(new DiagnosticPatchInfo.FoundClass(className.substring(className.lastIndexOf("\\")+1), className, jarName));
        }

        return new DiagnosticPatchInfo( String.format("%s-diagnostic-patch-tce%s.zip", ticketNumber,
                tceNumber), generateSuggestedInstructions(ticketNumber, tceNumber, branchName, foundClassList), foundClassList );
    }

    private String generateSuggestedInstructions(String ticketNumber, String tceNumber, String branchName, List<DiagnosticPatchInfo.FoundClass> classFileList) {
        List<String> instructions = new ArrayList<>();

        instructions.add("Diagnostic Patch Instructions");
        instructions.add("");
        instructions.add(String.format("Ticket #: %s", ticketNumber));
        instructions.add(String.format("TCE version: %s", tceNumber));
        instructions.add(String.format("Branch: %s", branchName));
        instructions.add("");
        instructions.add("- First Backup the following class files:");
        for( DiagnosticPatchInfo.FoundClass filePath : classFileList ){
            instructions.add(String.format("    - %s", filePath.getClassPath()));
        }
        instructions.add("- Second copy the classes contained in the zip file in their respectives directories:");
        for( DiagnosticPatchInfo.FoundClass filePath : classFileList ){
            instructions.add(String.format("    - %s -> %s\\%s", filePath.getClassName(), filePath.getJarFileSimpleName(), filePath.getPackagePath()));
        }

        return Strings.join(instructions, '\n');
    }

    public static String getCurrentGitBranch(File projectDir) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec( "git rev-parse --abbrev-ref HEAD", null, projectDir );
        process.waitFor();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader( process.getInputStream() ) );

        return reader.readLine();
    }

    public String getJarNameForClass( String classPath, List<String> jarList ) throws IOException {
        String jarName = "";

        for( String jarFileName : jarList ) {
            String tmpClassPath = classPath.substring(classPath.lastIndexOf("target\\classes\\")+15);
            ZipFile file = new ZipFile(jarFileName);
            if( isClassInJarFile(tmpClassPath, file) ) {
                jarName = jarFileName;
                break;
            }
        }

        return jarName;
    }

    public boolean isClassInJarFile(String name, ZipFile file) throws IOException {
        String tmpName = name.replace("\\", "/");
        for (ZipEntry e : Collections.list(file.entries())) {
            if (e.getName().endsWith(tmpName)) {
                return true;
            }
        }

        return false;
    }
}