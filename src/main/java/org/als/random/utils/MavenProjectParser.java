package org.als.random.utils;

import org.als.random.RandomLogger;
import org.als.random.domain.*;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class MavenProjectParser {

    public MavenProject parseProject(String projectDir, String initialProject) {
        File file = new File(initialProject+"/pom.xml");
        MavenProject mavenProject = null;
        if(file.exists() && file.isFile()) {
            mavenProject = createMavenProject(file);

            MavenProjectFileSystemFinder systemFinder = new MavenProjectFileSystemFinder(projectDir);
            Map<String, File> pomFileMap = systemFinder.getPomFileMap();

            Map<String, MavenProject> projectMap = new HashMap<>();
            for( Map.Entry<String, File> entry : pomFileMap.entrySet() ) {
                String pomFilePath = entry.getKey();
                File pomFile = entry.getValue();

                MavenProject project = createMavenProject(pomFile);
                if(Objects.nonNull(project)) {
                    projectMap.put(getRelativeProjectDirectory(projectDir, pomFilePath), project);
                }
            }

            loadMavenProjectModules( mavenProject, projectMap );
        }

        return mavenProject;
    }

    private void loadMavenProjectModules( MavenProject project, Map<String, MavenProject> projectMap ) {
        Map<String, MavenModule> newModuleMap = new HashMap<>();

        if( Objects.nonNull(project.getModuleMap()) ) {
            for (Map.Entry<String, MavenModule> moduleEntry : project.getModuleMap().entrySet()) {
                String modulePath = moduleEntry.getKey().replace("../", "");

                for (Map.Entry<String, MavenProject> entry : projectMap.entrySet()) {
                    String projectPath = entry.getKey().replace("\\pom.xml", "").replace("\\", "/");
                    MavenProject mavenProject = entry.getValue();

                    if (projectPath.endsWith(modulePath)) {
                        MavenModule.MavenModuleBuilder mavenModulebuilder = MavenModule.builder();
                        mavenProject.setParentProject(project);
                        mavenModulebuilder.project(mavenProject);

                        newModuleMap.put(modulePath, mavenModulebuilder.build());

                        loadMavenProjectModules(mavenProject, projectMap);
                        break;
                    }
                }

                if( !newModuleMap.containsKey(modulePath) ){
                    newModuleMap.put(modulePath, null);
                }
            }

            project.setModuleMap(newModuleMap);
        }
    }

    public String getRelativeProjectDirectory( String projectDir, String pomFilePath ) {
        return pomFilePath.replace("/pom.xml", "").replace(projectDir, "").trim();
    }

    public MavenProject createMavenProject(File file) {
        MavenProject project = null;
        MavenProject.MavenProjectBuilder builder = MavenProject.builder();
        if( file.exists() ) {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            try {
                Model model = reader.read(new FileReader(file));

                builder.directory(file.getAbsolutePath().replace("/pom.xml", ""));
                builder.name(model.getName());
                builder.groupId(model.getGroupId());
                builder.artifactId(model.getArtifactId());
                builder.modelVersion(model.getModelVersion());
                builder.version(model.getVersion());
                builder.packaging(model.getPackaging());

                project = builder.build();

                addMavenDependencies(project, model.getDependencies());
                addMavenDependencyManagement(project, model.getDependencyManagement());
                initializeModules(project, model.getModules());

                addMavenProfiles( project, model );
            } catch (IOException | XmlPullParserException e) {
                throw new RuntimeException(e);
            }
        }

        return project;
    }

    private void addMavenProfiles( MavenProject project, Model model ) {
        if( Objects.nonNull(model.getProfiles())){
            List<MavenProfile> profileList = new ArrayList<>();
            for (Profile profile : model.getProfiles()) {
                MavenProfile mavenProfile = new MavenProfile();

                mavenProfile.setId(profile.getId());
                if( Objects.nonNull(profile.getActivation()) )
                    mavenProfile.setActiveByDefault(profile.getActivation().isActiveByDefault());
                addMavenDependencies(mavenProfile, profile.getDependencies());
                addMavenDependencyManagement(mavenProfile, profile.getDependencyManagement());
                initializeModules(mavenProfile, profile.getModules());

                profileList.add(mavenProfile);
            }
            project.setProfileList(profileList);
        }
    }

    private void addMavenDependencies( MavenProjectInterface project, List<Dependency> dependencies ) {
        List<MavenDependency> dependencyList = new ArrayList<>();
        for(Dependency dependency : dependencies) {
            MavenDependency mavenDependency = MavenDependency.builder()
                    .groupId(dependency.getGroupId())
                    .artifactId(dependency.getArtifactId())
                    .version(dependency.getVersion())
                    .build();

            dependencyList.add(mavenDependency);
        }
        project.setDependencyList(dependencyList);
    }

    private void addMavenDependencyManagement(MavenProjectInterface project, DependencyManagement dependencyManagement) {
        List<MavenDependencyManagement> dependencyManagementList = new ArrayList<>();
        if (Objects.nonNull(dependencyManagement)) {
            for (Dependency dependency : dependencyManagement.getDependencies()) {
                MavenDependencyManagement mavenDependency = MavenDependencyManagement.builder()
                        .groupId(dependency.getGroupId())
                        .artifactId(dependency.getArtifactId())
                        .version(dependency.getVersion())
                        .build();

                dependencyManagementList.add(mavenDependency);
            }
            project.setDependencyManagementList(dependencyManagementList);
        }
    }

    private void initializeModules(MavenProjectInterface project, List<String> modules ) {
        for( String module : modules ) {
            project.addModulePath(module);
        }
    }

    public static void main( String[] args ) {
        RandomLogger logger = new RandomLogger(MavenProjectParser.class);
        String projectDir = "C:/Users/betoc/repositories/TCE7.1.0/teamconnectenterprise";
        String initialProject = "C:/Users/betoc/repositories/TCE7.1.0/teamconnectenterprise/Core/teamconnect-parent";
        MavenProject mavenProject = (new MavenProjectParser()).parseProject(projectDir, initialProject);
        logger.log(mavenProject);
    }
}
