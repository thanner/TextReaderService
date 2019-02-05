package br.edu.ufrgs.inf.bpm.rest.getElementsProcess;

import br.edu.ufrgs.inf.bpm.rest.ClientConnection;

public class MarkedTextClient {

    public boolean hasConnected() {
        return ClientConnection.makeConnectionTest(MarkedTextURLs.HasConnectedURL);
    }

    public String getMarkedText(String text) {
        return ClientConnection.makeConnection(MarkedTextURLs.GetMarkedTextURL, text);
    }

}
