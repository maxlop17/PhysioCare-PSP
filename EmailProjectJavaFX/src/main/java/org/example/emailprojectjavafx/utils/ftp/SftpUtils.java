package org.example.emailprojectjavafx.utils.ftp;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import io.github.cdimascio.dotenv.Dotenv;
import org.example.emailprojectjavafx.utils.services.ServiceUtils;

public class SftpUtils {
    /**
     * Method that connects to the server through SFTP with the provided credentials
     * @return the ChannelSftp with the connection to the session of JSch
     * @throws Exception if the connection was unsuccessful
     */
    public static ChannelSftp connectSFTP() throws Exception {
        Session session;
        Channel channel;
        ChannelSftp sftpChannel;
        Dotenv env = Dotenv.load();
        String user = env.get("SFTP_USER");
        String pass = env.get("SFTP_PASSWORD");
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
            System.out.println("Connected sftpChannel");
            return sftpChannel;

        } catch (Exception e) {
            throw new Exception();
        }
    }
}
