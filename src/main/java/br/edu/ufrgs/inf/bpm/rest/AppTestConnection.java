package br.edu.ufrgs.inf.bpm.rest;

import br.edu.ufrgs.inf.bpm.rest.getElementsProcess.MarkedTextClient;

public class AppTestConnection {

    public static void main(String[] args) {

        MarkedTextClient markedTextClient = new MarkedTextClient();
        System.out.println("Marked Text: " + markedTextClient.hasConnected());

    }

}
