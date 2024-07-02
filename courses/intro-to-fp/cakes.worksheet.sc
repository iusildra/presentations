trait Component
case class Position(x: Int, y: Int) extends Component
case class Velocity(x: Int, y: Int) extends Component
trait System:
  def components: List[Component]

val system: System = new System {
  def components: List[Component] = (Position(0, 0) :: Velocity(1, 1) :: Nil)
}

def findComponent[T](cls: Class[T]) =
  system.components.collectFirst{
    case x if x.getClass() == cls => x
  }

findComponent(classOf[Position])