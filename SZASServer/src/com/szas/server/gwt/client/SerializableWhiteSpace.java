package com.szas.server.gwt.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.szas.data.FieldDataTuple;
import com.szas.data.FieldIntegerBoxDataTuple;
import com.szas.data.FieldIntegerBoxTuple;
import com.szas.data.FieldTextBoxTuple;
import com.szas.data.FilledQuestionnaireTuple;
import com.szas.data.QuestionnaireTuple;
import com.szas.data.FieldTextAreaDataTuple;
import com.szas.data.FieldTextAreaTuple;
import com.szas.data.UserTuple;
import com.szas.sync.local.LocalTuple;
import com.szas.sync.remote.RemoteTuple;

public class SerializableWhiteSpace implements IsSerializable {
	RemoteTuple<UserTuple> dummy1;
	LocalTuple<UserTuple> dummy2;
	QuestionnaireTuple dummy3;
	FieldDataTuple d1;
	FieldIntegerBoxDataTuple d2;
	FieldIntegerBoxTuple d3;
	FieldTextBoxTuple d4;
	FilledQuestionnaireTuple d5;
	QuestionnaireTuple d6;
	FieldTextAreaDataTuple d7;
	FieldTextAreaTuple d8;
}
