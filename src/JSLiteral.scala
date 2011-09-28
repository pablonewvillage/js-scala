import scala.virtualization.lms.common._

import java.io.PrintWriter
  
trait JSLiteral extends Base with EmbeddedControls {
  type JSLiteral <: Row[Rep]
  override def __new[T, R[x]](args: (String, T => (R[t] forSome{type t}))*): T =
    newJSLiteral(args.map(_.asInstanceOf[(String, Rep[JSLiteral] => (Rep[t] forSome{type t}))]): _*).asInstanceOf[T]
  def newJSLiteral(args: (String, Rep[JSLiteral] => (Rep[t] forSome{type t}))*): Rep[JSLiteral]
  
  abstract class JSLiteralOps {
    def applyDynamic[T](n: String)(as: AnyRef*): Rep[T]
    def selectDynamic[T](field: String): Rep[T]
  }
  implicit def jsLiteralOps(receiver: Rep[JSLiteral]): JSLiteralOps
}

trait JSLiteralExp extends JSLiteral with BaseExp {
  trait JSLiteral extends Row[Rep]
  case class JSLiteralExp(args: List[(String, Exp[Any])]) extends Exp[JSLiteral]
  class JSLiteralOpsImpl(val receiver: Exp[JSLiteral]) extends JSLiteralOps {
    def applyDynamic[T](n: String)(as: AnyRef*): Rep[T] =
      sys.error(receiver.toString +
        ".applyDynamic(%1s)(%2s)".format(n, as.mkString("(", ",", ")")))

    def selectDynamic[T](field: String): Rep[T] =
      sys.error(receiver.toString + ".selectDynamic(%1s)".format(field))
  }
  implicit def jsLiteralOps(receiver: Exp[JSLiteral]): JSLiteralOps = new JSLiteralOpsImpl(receiver)
  def newJSLiteral(args: (String, Rep[JSLiteral] => (Rep[t] forSome{type t}))*): Exp[JSLiteral] =
    JSLiteralExp(args.toList collect { case (name, arg: Exp[_]) => (name, arg) })
}