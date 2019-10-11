import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * The Class Flappy.
 */
public class Flappy extends Application {

	/** Define the static values before the animation starts **/

	// initial bird, tube, ground position
	public final static int INIT_BIRD_X_POS = 50;
	public final static int INIT_BIRD_Y_POS = 200;
	public final static int INIT_TUBE_X_POS = 348;
	public final static int TUBE_HEIGHT_MUL = 90;
	public final static int INIT_TUBE_HEIGHT = -250;
	public final static int INTER_TUBE_SPACE = 430;
	public final static int GROUND_SIZE = 400;
	public final static double GROUND_Y_FACTOR = 0.9;
	public final static int GROUND_WIDTH_FACTOR = 2;

	// button and score positions
	public final static int START_BUTTON_Y_POS = 20;
	public final static int STOP_BUTTON_Y_POS = 20;
	public final static int START_BUTTON_X_POS = 130;
	public final static int STOP_BUTTON_X_POS = 240;
	public final static int SCORE_X_POS = 180;
	public final static int SCORE_Y_POS = 130;
	public final static int SCORE_FONT_SIZE = 80;
	public final static String SCORE_FONT = "Verdana";

	// stage width and height, time interval, tube shift and flap motion
	public final static int STAGE_WIDTH = 400;
	public final static int STAGE_HEIGHT = 400;
	public final static int TIME_INTERVAL = 2000;	//interval in ms
	public final static int TUBE_SHIFT_GOAL = -460;
	public final static int FLAP_UPWARD_MOTION = -60;
	public final static int FLAP_DOWNWARD_MOTION = 150;
	public final static int GROUND_SHIFT_LEFT_DIST = -400;

	/**
	 * The background. declare ImageViews, Buttons, Transitions, and Score class
	 * variables
	 */
	private ImageView bkgrd = null;
	private ImageView flappy = null;
	private ImageView ground = null;
	private ImageView topTube = null;
	private ImageView bottomTube = null;
	private Button start_button = null;
	private Button stop_button = null;
	private Group root = null;
	private Timeline tubeAnimation = null;
	private TranslateTransition moveGround = null;
	private SequentialTransition st = null;
	private Text score = null;
	private int count = 0;
	private Integer countObject = null;;

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Create a Group
		root = new Group();

		// Lab_1 code is here: add background, bird, ground and
		// "start" and "stop" button.

		// background image node
		bkgrd = new ImageView("background.png");
		bkgrd.setPreserveRatio(true);

		// flappy bird node
		flappy = new ImageView("flappy.png");
		flappy.setLayoutX(INIT_BIRD_X_POS);
		flappy.setLayoutY(INIT_BIRD_Y_POS);

		// top tube node
		topTube = new ImageView("obstacle_top.png");
		topTube.setLayoutX(INIT_TUBE_X_POS);
		int topTubeLocation = (int) (INIT_TUBE_HEIGHT + java.lang.Math.random() * TUBE_HEIGHT_MUL);
		topTube.setLayoutY(topTubeLocation);
		/*
		 * make random integer to establish the changing locations of the top and bottom
		 * tubes
		 */

		// bottom tube node
		bottomTube = new ImageView("obstacle_bottom.png");
		bottomTube.setLayoutX(INIT_TUBE_X_POS);
		bottomTube.setLayoutY(topTubeLocation + INTER_TUBE_SPACE);

		/*
		 * bottom tube is a fixed distance from the top tube based on the random integer
		 * location of the top tube
		 */

		// ground node
		ground = new ImageView("ground.png");
		ground.setLayoutY(GROUND_SIZE * GROUND_Y_FACTOR);
		ground.setFitWidth(GROUND_SIZE * GROUND_WIDTH_FACTOR);

		// start button node
		start_button = new Button("Start");
		start_button.setLayoutX(START_BUTTON_X_POS);
		start_button.setLayoutY(START_BUTTON_Y_POS);

		// stop button node
		stop_button = new Button("Stop");
		stop_button.setLayoutX(STOP_BUTTON_X_POS);
		stop_button.setLayoutY(STOP_BUTTON_Y_POS);

		// score display node
		score = new Text("0");
		score.setLayoutX(SCORE_X_POS);
		score.setLayoutY(SCORE_Y_POS);
		score.setFont(Font.font(SCORE_FONT, SCORE_FONT_SIZE));
		score.setFill(Color.WHITE);

		// Add all nodes to the root.
		root.getChildren().add(bkgrd);
		root.getChildren().add(ground);
		root.getChildren().add(flappy);
		root.getChildren().add(topTube);
		root.getChildren().add(bottomTube);
		root.getChildren().add(start_button);
		root.getChildren().add(stop_button);
		root.getChildren().add(score);

		// Add Event Handlers
		addActionEventHandler();
		//addMouseEventHandler();
		addTubeListener();

		// Create scene and add to stage
		Scene scene = new Scene(root, STAGE_HEIGHT, STAGE_WIDTH);
		primaryStage.setScene(scene);
		primaryStage.show();

	}


	/**
	 * Lab_2 code is here: implement translate animation for ground
	 * and sequential animation for bird.
	 * Moving ground.
	 * establish moving ground animation
	 */
	public void movingGround() {
		moveGround = new TranslateTransition(new Duration(TIME_INTERVAL), ground);
		moveGround.setToX(GROUND_SHIFT_LEFT_DIST);
		moveGround.setInterpolator(Interpolator.LINEAR);
		moveGround.setCycleCount(Timeline.INDEFINITE);
		moveGround.play();

	}

	/**
	 * Flap.
	 * flapping motion for the flappy bird
	 */
	public void flap() {


		// upward motion
		TranslateTransition up = new TranslateTransition(new Duration(TIME_INTERVAL), flappy);
		up.setByY(FLAP_UPWARD_MOTION);
		up.setInterpolator(Interpolator.EASE_OUT);

		// downward motion
		TranslateTransition down = new TranslateTransition(new Duration(TIME_INTERVAL), flappy);
		down.setByY(FLAP_DOWNWARD_MOTION);
		down.setInterpolator(Interpolator.EASE_IN);

		// establish flapping animation
		st = new SequentialTransition();
		st.getChildren().addAll(up, down);
		st.setCycleCount(1);
		st.play();

	}

	/**
	 * Setup animation for the moving tubes
	 */ 
	public void setupTubeAnimation() {
		// define key values and duration of animation
		KeyValue top = new KeyValue(topTube.translateXProperty(), TUBE_SHIFT_GOAL);
		KeyValue bottom = new KeyValue(bottomTube.translateXProperty(), TUBE_SHIFT_GOAL);
		Duration time = new Duration(TIME_INTERVAL);

		// redefine the heights of the tubes for each iteration
		EventHandler<ActionEvent> onFinished = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {

				// resets location of the tubes with random integer
				int topTubeLocation = (int) (INIT_TUBE_HEIGHT + java.lang.Math.random() * TUBE_HEIGHT_MUL);
				topTube.setLayoutY(topTubeLocation);
				bottomTube.setLayoutY(topTubeLocation + INTER_TUBE_SPACE);

				// increments the score to display
				count++;
				countObject = new Integer(count);
				score.setText(countObject.toString());

			}
		};

		// establish tube animation
		KeyFrame kFrame = new KeyFrame(time, onFinished, top, bottom);
		tubeAnimation = new Timeline();
		tubeAnimation.setCycleCount(Timeline.INDEFINITE);
		tubeAnimation.getKeyFrames().add(kFrame);
		tubeAnimation.play();

	}

	/**
	 * Check collisions with the tubes and the flappy bird
	 */
	public void checkCollision() {

		// defines the bounds of the bird and the tubes
		Bounds birdBounds = flappy.localToScene(flappy.getBoundsInLocal());
		Bounds topTubeBounds = topTube.localToScene(topTube.getBoundsInLocal());
		Bounds bottomTubeBounds = bottomTube.localToScene(bottomTube.getBoundsInLocal());

		// checks for overlap/collision between the bounds
		if (birdBounds.intersects(topTubeBounds) || birdBounds.intersects(bottomTubeBounds)) {
			tubeAnimation.stop();
			moveGround.stop();
			flap();
			st.stop();
		}

	}

	/**
	 * Adds the action event handler. (Lab 3 and later)
	 */
	private void addActionEventHandler() {
		// establishes actions by start button
		start_button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				/* Flappy bird node is added to the group after start button is pressed in order
				start flapping animation only when start is pressed */
				addMouseEventHandler();
				movingGround();
				setupTubeAnimation();
			}
		});

		// establishes actions by stop button
		stop_button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				tubeAnimation.stop();
				moveGround.stop();
				flap();
				st.stop();
			}
		});
	}

	/**
	 * Adds the mouse event handler.
	 */
	private void addMouseEventHandler() {
		// established mouse click to flapping motion by flappy bird
		root.onMouseClickedProperty().set(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				flap();
			}
		});
	}

	/**
	 * Adds the tube listener. 
	 * watches leftward motion of the tubes and checks 
	 * for collision for each time it moves left
	 */
	public void addTubeListener() {
		DoubleProperty tubeTopX = topTube.translateXProperty();
		tubeTopX.addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> o, Number oldValue, Number newValue) {
				checkCollision();
			}
		});
		DoubleProperty tubeBottomX = bottomTube.translateXProperty();
		tubeBottomX.addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> o, Number oldValue, Number newValue) {
				checkCollision();
			}
		});
	}

	/**
	 * The main method.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		Application.launch(args);
	}

}
