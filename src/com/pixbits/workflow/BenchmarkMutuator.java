package com.pixbits.workflow;

public class BenchmarkMutuator<T extends WorkflowData> implements Mutuator<T>
{
  private final Mutuator<T> inner;
  
  BenchmarkMutuator(Mutuator<T> inner)
  {
    this.inner = inner;
  }
  
  public T apply(T data)
  {
    long start = System.nanoTime();
    T ret = inner.apply(data);
    long end = System.nanoTime();
    System.out.println("Mutuator "+inner.getClass().getName()+" required "+(end-start)/1000000.0+" msec");
    return ret;
  }
}
