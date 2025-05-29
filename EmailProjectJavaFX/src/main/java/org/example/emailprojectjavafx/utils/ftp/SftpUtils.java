package org.example.emailprojectjavafx.utils.ftp;

import com.jcraft.jsch.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.commons.net.ftp.FTPSClient;
import org.example.emailprojectjavafx.utils.Utils;
import org.example.emailprojectjavafx.utils.services.ServiceUtils;

import java.io.File;

public class SftpUtils {
    private static ChannelSftp sftp;

    /**
     * Method that connects to the server through SFTP with the provided credentials
     * @return the ChannelSftp with the connection to the session of JSch
     * @throws Exception if the connection was unsuccessful
     */
    private static ChannelSftp connectSFTP() throws Exception {
        Session session;
        Channel channel;
        ChannelSftp sftpChannel;
        String user = Dotenv.load().get("SFTP_USER");
        String pass = Dotenv.load().get("SFTP_PASSWORD");
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(user, ServiceUtils.SERVER, 22);
            session.setPassword(pass);
            // Configuration to not validate the host key
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            System.out.println("Connected");

            // Establish connection
            channel = session.openChannel("sftp");
            channel.connect();
            sftpChannel = (ChannelSftp) channel;
            System.out.println("Connected sftpChannel to the server");
            return sftpChannel;

        } catch (Exception e) {
            throw new Exception();
        }
    }

    public static void uploadFTP(){
        boolean recordsExist = true;
        try {
            sftp = connectSFTP();

            sftp.cd("ftp/records");
        } catch (SftpException ex){
            if(ex.id == ChannelSftp.SSH_FX_NO_SUCH_FILE){
                recordsExist = false;
            }
        } catch (Exception e) {
            Utils.showAlert("Error", "Error connecting to the Server", 2);
        }

        try {
            if (!recordsExist) {
                sftp.mkdir("ftp/records");
            }

            File records = new File("resources/records");
            File[] files = records.listFiles();

            if(files != null){
                for(File file : files){
                    if(file.isFile()){
                        sftp.put(file.getAbsolutePath(), file.getName());
                    }
                }
            } else {
                Utils.showAlert("Error", "The folter does not exist or there are no records to upload", 2);
            }


        } catch (SftpException e) {
            throw new RuntimeException(e);
        }
    }
}
