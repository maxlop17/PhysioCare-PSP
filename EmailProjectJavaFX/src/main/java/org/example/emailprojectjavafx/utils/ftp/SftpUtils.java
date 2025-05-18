package org.example.emailprojectjavafx.utils.ftp;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class SftpUtils {
    /**
     * Method that connects to the server through SFTP with the provided credentials
     * @param server with the name of the server address to connect to
     * @param user with the username to do the login process
     * @param pass with the password associated to the username
     * @return the ChannelSftp with the connection to the session of JSch
     * @throws Exception if the connection was unsuccessful
     */
    public static ChannelSftp connectSFTP(String server, String user, String pass) throws Exception {
        Session session;
        Channel channel;
        ChannelSftp sftpChannel;
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(user, server, 22);
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
