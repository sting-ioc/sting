package sting.processor;

/**
 * The supported v1 interceptor binding value kinds.
 */
public enum BindingValueKind
{
  /**
   * A string-valued annotation member.
   */
  STRING,
  /**
   * A boolean-valued annotation member.
   */
  BOOLEAN,
  /**
   * A byte-valued annotation member.
   */
  BYTE,
  /**
   * A short-valued annotation member.
   */
  SHORT,
  /**
   * An int-valued annotation member.
   */
  INT,
  /**
   * A long-valued annotation member.
   */
  LONG,
  /**
   * A float-valued annotation member.
   */
  FLOAT,
  /**
   * A double-valued annotation member.
   */
  DOUBLE,
  /**
   * A char-valued annotation member.
   */
  CHAR,
  /**
   * An enum-valued annotation member.
   */
  ENUM,
  /**
   * A class-valued annotation member.
   */
  CLASS,
  /**
   * A value shape that v1 records but does not support for lifecycle binding.
   */
  UNSUPPORTED
}
