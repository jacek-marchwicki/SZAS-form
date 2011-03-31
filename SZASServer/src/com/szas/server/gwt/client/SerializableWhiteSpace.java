package com.szas.server.gwt.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.szas.data.UserTuple;
import com.szas.sync.local.LocalTuple;
import com.szas.sync.remote.RemoteTuple;

public class SerializableWhiteSpace implements IsSerializable {
	RemoteTuple<UserTuple> dummy1;
	LocalTuple<UserTuple> dummy2;
}
