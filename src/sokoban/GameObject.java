package sokoban;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

import javax.swing.Timer;

import sokoban.util.Direction;
import sokoban.util.Pair;

/**
 * Klasa reprezentująca dowolny obiekt mapy tj. skrzynka, gracz czy ściana. Każdy z obiektów ma określone cechy tj. możliwość przesuwania oraz możliwość przesuwania innych obiektów przez ten obiekt.
 * 
 * @author Maciej Budrowski
 *
 */
@SuppressWarnings("serial")
public class GameObject implements Cloneable {

	public static final int MOVE_TIME_MS = 60;
	public static final int MOVE_TICK_MS = 15;

	/**
	 * Klasa pełniąca funkcje zegara do płynnego przesuwania obiektów po planszy. Każdy cykl zegara przesuwa obiekt o określoną liczbę pikseli, a po zakończeniu przesuwania zmienia faktyczną pozycję obiektu na mapie.
	 * 
	 * @author Maciej Budrowski
	 *
	 */
	protected class Mover extends Timer implements ActionListener {
		protected int ticks = 0;
		protected Direction moveDirection;

		public Mover(Direction dir) {
			super(MOVE_TICK_MS, null);
			GameObject.this.transform = new AffineTransform();
			moveDirection = dir;
			addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (GameObject.this.parent.hasEnded()) {
				stop();
				return;
			}
			ticks += MOVE_TICK_MS;
			double pixels = (double) MOVE_TICK_MS / MOVE_TIME_MS * Resources.TILESIZE;
			GameObject.this.transform.translate(moveDirection.getOffsetX() * pixels,
					moveDirection.getOffsetY() * pixels);
			if (ticks >= MOVE_TIME_MS) {
				stop();
				GameObject.this.x = GameObject.this.x + moveDirection.getOffsetX();
				GameObject.this.y = GameObject.this.y + moveDirection.getOffsetY();
				GameObject.this.transform = null;
				GameObject.this.parent.checkCompletion();
				GameObject.this.isMoving = false;
			}
			GameObject.this.parent.repaint();
		}

	}

	protected boolean canMoveThrough, canBeMoved;
	protected int x, y;
	protected Board parent;
	protected AffineTransform transform;
	protected boolean isMoving = false;

	public GameObject(Board parent, boolean canMoveThrough, boolean canBeMoved) {
		this.parent = parent;
		this.canMoveThrough = canMoveThrough;
		this.canBeMoved = canBeMoved;
	}

	/**
	 * Konstruktor kopiujący.
	 * 
	 * @param proto Prototyp
	 */
	protected GameObject(GameObject proto) {
		this.parent = proto.parent;
		this.canMoveThrough = proto.canMoveThrough;
		this.canBeMoved = proto.canBeMoved;
		this.x = proto.x;
		this.y = proto.y;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new GameObject(this);
	}

	/**
	 * Metoda, która umieszcza obiekt o danym prototypie na podanej pozycji na mapie. Prototyp zostaje kopiowany przy użyciu metody clone(), a następnie stworzona instancja ma przypisane określone współrzędne na mapie.
	 * 
	 * @param prototype Prototyp umieszczanego obiektu
	 * @param x Współrzędna X na mapie
	 * @param y Współrzędna Y na mapie
	 * @return Instancja obiektu mapy.
	 */
	public static GameObject placeInstance(GameObject prototype, int x, int y) {
		if (prototype == null) {
			return null;
		}
		GameObject obj = null;
		try {
			obj = (GameObject) prototype.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		obj.x = x;
		obj.y = y;
		return obj;
	}

	/**
	 * Metoda inicjująca polecenie ruchu obiektu w danym kierunku.
	 * 
	 * @param dir Kierunek ruchu
	 */
	public void move(Direction dir) {
		move(dir, true);
	}

	/**
	 * Metoda pomocnicza do inicjacji polecenie ruchu obiektu w danym kierunku.
	 * 
	 * @param dir Kierunek ruchu
	 * @param initiatedMove Jeśli true, to znaczy, że ruch został rozpoczęty w tym obiekcie (np. obiekt gracza przesunięty poprzez naciśnięcie klawisza); jeśli false, to znaczy, że nastąpiła próba przesunięcia obiektu przez inny obiekt (np. obiekt skrzynki przesuwany przez obiekt gracza).
	 */
	protected synchronized void move(Direction dir, boolean initiatedMove) {
		if (!canMove(dir, initiatedMove)) {
			return;
		}
		int newX = x + dir.getOffsetX(), newY = y + dir.getOffsetY();
		if (!isMoving) {
			isMoving = true;
			Mover mover = new Mover(dir);
			mover.start();
			if (initiatedMove) {
				if (parent.getObjectAt(newX, newY) != null) {
					parent.getObjectAt(newX, newY).move(dir, false);
				}
			}
		}
	}

	/**
	 * Metoda pomocnicza informująca o tym, czy obiekt może poruszyć się w danym kierunku.
	 * 
	 * @param dir Kierunek ruchu
	 * @param initiatedMove Jeśli true, to znaczy, że ruch został rozpoczęty w tym obiekcie (np. obiekt gracza przesunięty poprzez naciśnięcie klawisza); jeśli false, to znaczy, że nastąpiła próba przesunięcia obiektu przez inny obiekt (np. obiekt skrzynki przesuwany przez obiekt gracza).
	 * @return True, jeśli może się poruszyć, w przeciwieństwie - false.
	 */
	protected boolean canMove(Direction dir, boolean initiatedMove) {
		if (!canBeMoved) {
			return false;
		}
		int newX = x + dir.getOffsetX(), newY = y + dir.getOffsetY();
		if (newX < 0 || newY < 0 || newX >= parent.getWidth() || newY >= parent.getHeight()) {
			return false;
		}
		if (parent.getObjectAt(newX, newY) == null) {
			return true;
		}
		if (parent.getObjectAt(newX, newY).canMoveThrough) {
			return true;
		}
		if (initiatedMove) {
			return parent.getObjectAt(newX, newY).canMove(dir, false);
		}
		return false;
	}

	/**
	 * Metoda zwracająca współrzędną X obiektu na mapie.
	 * @return Współrzędna X
	 */
	public int getX() {
		return x;
	}

	/**
	 * Metoda zwracająca współrzędną Y obiektu na mapie.
	 * @return Współrzędna Y
	 */
	public int getY() {
		return y;
	}

	/**
	 * Metoda zwracająca współrzędne obiektu na mapie w postaci pary liczb.
	 * @return Para liczb - współrzędna X, współrzędna Y
	 */
	public Pair<Integer, Integer> getCoordinates() {
		return new Pair<>(x, y);
	}

	/**
	 * Metoda rysująca obiekt na ekranie.
	 * 
	 * @param g Kontekst graficzny.
	 * @param image Obraz obiektu.
	 */
	public void draw(Graphics2D g, Image image) {
		if (transform == null) {
			g.drawImage(image, Resources.TILESIZE * getX(), Resources.TILESIZE * getY(), Resources.TILESIZE,
					Resources.TILESIZE, null);
			return;
		}
		AffineTransform oldTr = g.getTransform();
		g.transform(transform);
		g.drawImage(image, Resources.TILESIZE * getX(), Resources.TILESIZE * getY(), Resources.TILESIZE,
				Resources.TILESIZE, null);
		g.setTransform(oldTr);
	}
}
