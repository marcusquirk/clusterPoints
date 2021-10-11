import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.geom.Ellipse2D
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.math.*
import kotlin.random.Random

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

fun randPoint(x1:Double, x2:Double, y1:Double, y2:Double):() -> Point{
    return({-> Point((x1 +(x2-x1) * Random.nextFloat()).toDouble(), (y1 + (y2-y1) * Random.nextFloat()).toDouble())})
}

// Problem 3
fun makeClusterPointGenerator (rng: Random, center: Point, distance: Double): () -> Point {
    fun clusterPointGenerator(): Point {
        val angle = 2.0*PI*rng.nextDouble()
        val distance = distance * ln(rng.nextDouble())
        val x = center.x + distance * cos(angle)
        val y = center.y + distance * sin(angle)
        return Point(x,y)
    }
    return ::clusterPointGenerator
}

fun createClusters (numberOfClusters: Int, pointsPerCluster: Int, distanceFromCenter: Double): Unit{
    for (i in 0 until numberOfClusters){
        createCluster(pointsPerCluster, distanceFromCenter, i)
    }
}

class Cluster (center: Point, numPoints: Int, averageDistance: Double) {
    val clusterID: Int = center.cluster
    val points: MutableList<Point> = MutableList(numPoints)

    init {
        val randPointMaker = outerFun(kotlin.random.Random(System.nanoTime()), center, averageDistance)
        for (i in 1 until numPoints) {
            points.add(randPointMaker())

        }
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

    val center: Point = randPoint(1.0, 4.0, 3.0, 8.0)()
    val cluster = Cluster(center, 5, 2.0)
} // main
