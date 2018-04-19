package org.rikarika.pqs.ps4.net.client;

@FunctionalInterface
public interface FourInARowNetRegisterEventListener {
  public void playerIdFromServer(FourInARowNetClient client, int id);
}
