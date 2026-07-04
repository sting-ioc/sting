package sting.doc.examples.interceptors;

import sting.Injectable;
import sting.interceptors.After;
import sting.interceptors.AfterException;
import sting.interceptors.Before;
import sting.interceptors.MethodName;
import sting.interceptors.Result;
import sting.interceptors.ServiceType;
import sting.interceptors.Thrown;

@Injectable
public final class TimingInterceptor {
    @Before
    public void before(@ServiceType final String serviceType, @MethodName final String methodName) {}

    @After
    public void after(
            @ServiceType final String serviceType, @MethodName final String methodName, @Result final Object result) {}

    @AfterException
    public void afterException(
            @ServiceType final String serviceType,
            @MethodName final String methodName,
            @Thrown final Throwable thrown) {}
}
