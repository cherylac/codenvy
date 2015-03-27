/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2015] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.im.cli.command;

import com.codenvy.im.artifacts.CDECArtifact;
import com.codenvy.im.backup.BackupConfig;
import com.codenvy.im.service.InstallationManagerService;
import com.codenvy.im.service.UserCredentials;
import org.apache.felix.service.command.CommandSession;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.restlet.resource.ResourceException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.testng.Assert.assertEquals;

/** @author Dmytro Nochevnnov */
public class TestRestoreCommand extends AbstractTestCommand {
    private AbstractIMCommand spyCommand;

    @Mock
    private InstallationManagerService mockInstallationManagerProxy;
    @Mock
    private CommandSession             commandSession;

    private UserCredentials credentials;

    private BackupConfig testBackupConfig;

    private Path   testBackupFile      = Paths.get("test/backup/directory/backup.tar.gz");
    private String testArtifact        = CDECArtifact.NAME;

    @BeforeMethod
    public void initMocks() throws IOException {
        MockitoAnnotations.initMocks(this);

        spyCommand = spy(new RestoreCommand());
        spyCommand.service = mockInstallationManagerProxy;

        performBaseMocks(spyCommand, true);

        credentials = new UserCredentials("token", "accountId");
        doReturn(credentials).when(spyCommand).getCredentials();
    }


    @Test
    public void testRestore() throws Exception {
        testBackupConfig = new BackupConfig().setArtifactName(testArtifact)
                                             .setBackupFile(testBackupFile);

        String okServiceResponse = "{\n"
                                   + "  \"status\" : \"OK\"\n"
                                   + "}";

        doReturn(okServiceResponse).when(mockInstallationManagerProxy).restore(testBackupConfig);

        CommandInvoker commandInvoker = new CommandInvoker(spyCommand, commandSession);
        commandInvoker.argument("backup", testBackupFile.toString());

        CommandInvoker.Result result = commandInvoker.invoke();
        String output = result.disableAnsi().getOutputStream();
        assertEquals(output, okServiceResponse + "\n");
    }

    @Test
    public void testRestoreThrowsError() throws Exception {
        testBackupConfig = new BackupConfig().setArtifactName(testArtifact)
                                             .setBackupFile(testBackupFile);

        String expectedOutput = "{\n"
                                + "  \"message\" : \"Server Error Exception\",\n"
                                + "  \"status\" : \"ERROR\"\n"
                                + "}";

        doThrow(new ResourceException(500, "Server Error Exception", "Description", "localhost"))
            .when(mockInstallationManagerProxy).restore(testBackupConfig);

        CommandInvoker commandInvoker = new CommandInvoker(spyCommand, commandSession);
        commandInvoker.argument("backup", testBackupFile.toString());

        CommandInvoker.Result result = commandInvoker.invoke();
        String output = result.disableAnsi().getOutputStream();
        assertEquals(output, expectedOutput + "\n");
    }
}