package com.pixbits.workflow;

import java.util.function.Consumer;

public interface Sink<T extends Data> extends Consumer<T> { }
