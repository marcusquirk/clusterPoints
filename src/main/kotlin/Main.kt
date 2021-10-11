import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.geom.Ellipse2D
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.math.pow

object ClusterConstants {
    const val WIDTH  = 512
    const val HEIGHT = 512
    const val TITLE  = "Clusters"
    val BG_COLOUR = Color(248, 224, 112)
} // ClusterConstants

data class Rectangle(
    val xMin: Double, val yMin: Double,
    val xMax: Double, val yMax: Double
)

class PictureCanvas( private val bounds: Rectangle, private val bg: Color) : JPanel() {
    init { this.background = bg }
    override fun paintComponent (g: Graphics) : Unit {
        super.paintComponent(g)
        val g2D = g as Graphics2D
        val w = this.width
        val h = this.height
        val translate = AffineTransform()
        translate.translate(-bounds.xMin, -bounds.yMin)
        val sx = w / (bounds.xMax - bounds.xMin)
        val sy = h / (bounds.yMax - bounds.yMin)
        val scale = AffineTransform()
        scale.scale(sx, sy)
        val transform = AffineTransform()
        transform.concatenate(scale)
        transform.concatenate(translate)
        val centerX = (bounds.xMax + bounds.xMin) / 2
        val centerY = (bounds.yMax + bounds.yMin) / 2
        val radius = minOf(bounds.xMax - bounds.xMin, bounds.yMax - bounds.yMin) / 20
        val dot = Ellipse2D.Double(centerX - radius, centerY - radius, 2*radius, 2*radius)
        g2D.color = Color.RED
        g2D.fill(transform.createTransformedShape(dot))
    } // paintComponent()
    override fun repaint() {}
} // PictureCanvas

class PictureFrame(private val w: Int, private val h: Int, private val bg: Color, title: String) : JFrame(title) {
    init {
        this.setSize(w,h)
        this.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        this.isVisible = true
        val bounds = Rectangle(-1.0, -1.0, 1.0, 1.0)
        val panel = PictureCanvas(bounds, bg)
        this.contentPane = panel
    } // init
} //pictureFrame

class Point(val x: Double, val y: Double, var cluster: Int = -1){
    fun distance(p2: Point): Double {
        return ((x - p2.x).pow(2) + (y - p2.y).pow(2)).pow(0.5)
    }
}

fun main() {
    println("Good morning!")
    val picture = PictureFrame(
        ClusterConstants.WIDTH,
        ClusterConstants.HEIGHT,
        ClusterConstants.BG_COLOUR,
        ClusterConstants.TITLE
    )
    val thePoint = Point (0.0, 0.0)
    val aPoint   = Point (3.0, 4.0)
    println(thePoint.distance(aPoint))
} // main