package org.als.teamconnect.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class DownloadDiagnosticPatchRequest {
    String zipFileName;
    String patchInstructions;
    List<String> classFileList;
}
