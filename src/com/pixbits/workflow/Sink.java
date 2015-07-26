package com.pixbits.workflow;

import java.util.function.Consumer;

public interface Sink<T extends WorkflowData> extends Consumer<T> { }
