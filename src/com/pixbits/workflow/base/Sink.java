package com.pixbits.workflow.base;

import java.util.function.Consumer;

public interface Sink<T extends Data> extends Consumer<T> { }
