/**
 * Compile-time interceptor annotations used by Sting.
 *
 * <p>These annotations describe interceptor bindings, lifecycle phases, invocations, and lifecycle
 * metadata. Sting consumes them during annotation processing and emits direct proxy code; generated runtime code does
 * not inspect annotations reflectively.</p>
 *
 * <p>The {@link sting.interceptors.Around @Around} lifecycle phase receives a
 * {@link sting.interceptors.Invocation Invocation} parameter marked with
 * {@link sting.interceptors.Proceed @Proceed} so it can wrap, short-circuit, or replace arguments for the inner
 * invocation.</p>
 */
package sting.interceptors;
