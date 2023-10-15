**Identifying Clickable and non-clickable Elements on the screen**

**Problem Statement:**

In the realm of Android application usability, the challenge lies in distinguishing between clickable and non-clickable elements within diverse app interfaces. To address this, the task is to develop an Android application equipped with advanced detection capabilities. This application must be able to identify all interactive UI elements on any given Android app screen and draw green boxes around them. Simultaneously, the application should recognize non-clickable elements, such as section headings and textual content, and draw red boxes around them. This functionality is pivotal for providing users with clear visual cues, thereby enhancing their navigation experience and ensuring efficient interaction with mobile applications. The solution requires precision and efficiency in discerning between these UI components, guaranteeing an intuitive and user-friendly interface across various Android applications.

**Background:**

*Accessibility Services:*

Android Accessibility services are essential features designed to assist users with disabilities by providing alternative methods for interacting with the device and applications. These services enhance the overall accessibility of applications by offering tailored solutions, such as screen readers, voice commands, and gesture-based controls.

*Event Handling Mechanisms:*

Event handling in Android involves capturing and responding to user interactions, such as touch events and button clicks. These mechanisms enable developers to create responsive interfaces, where actions are triggered in response to user gestures.

*WindowManager and Overlay Permissions:*

WindowManager is a system service in Android responsible for managing the placement and behavior of UI elements on the screen. Overlay permissions grant applications the ability to draw UI elements, like floating buttons, on top of other apps' interfaces.

*UI Drawing and Layouts:*
UI drawing involves dynamically creating and positioning UI elements on the screen. Layouts define the arrangement and structure of these elements, ensuring a visually cohesive and organized user interface.

*UI Accessibility and Interaction:*
Accessible UI design focuses on creating interfaces that are usable by individuals with disabilities. Accessibility services facilitate alternative interaction methods, making applications accessible to users with diverse needs.

**Dependencies and Technologies Used:**

*Kotlin:* Backend logic and event handling.
*XML:* Frontend layout design and UI components.
*Android AccessibilityService API:* Detecting and processing accessibility events.
*Handler:* Managing delayed tasks and UI updates.
*WindowManager:* Drawing rectangles and visual elements on the screen.

**Approach (HLD):**

![Clickable_HLD](https://github.com/Anuhya27/Clickable-and-non-clickable-elements-on-any-screen/assets/56588616/bccb5bc7-7fa7-41e4-be65-dd80d97ada85)

- The user launches the application and grants necessary permissions, including overlay permission.
- The user taps the button on the main activity to initiate the UI detection process.
- The AccessibilityService listens for accessibility events triggered by user interactions across applications.
- Upon detecting an accessibility event, the AccessibilityService identifies clickable and non-clickable UI elements.
- Detected UI elements are sent to the UIDrawingService for visualization.
- The UIDrawingService receives UI element information and draws green rectangles around clickable elements and red rectangles around non-clickable elements on the screen.
- Users receive visual feedback as rectangles appear around different UI elements, indicating their clickability status.

**Low Level Design:**

*MainActivity:*
Main activity responsible for initiating the service and handling user interactions.

*Methods:*
- `onCreate(savedInstanceState: Bundle?)`: Initializes the main activity.
- `checkAccessibilityPermission()`: Checks and requests accessibility permission.
- `startService()`: Starts the FloatingToggleButtonService.
- `checkOverlayPermission()`: Checks and requests overlay permission.
- `onResume()`: Resumes activity and starts the service.

*FloatingToggleButtonService:*

Responsible for managing the floating button and drawing rectangles.

*Attributes:*
- `windowManager`: Manages the UI elements on the screen.
- `button`: Button for user interaction and service control.
*Methods:*
- `onCreate()`: Initializes the service and UI components.
- `setWindowManager(button: Button)`: Sets up the WindowManager for the button.
- `onDestroy()`: Cleans up resources and stops the service.
- `setupButtonTouchListener()`: Handles touch events for the button.

*MyAccessibilityService:*

Accessibility service for detecting UI elements and drawing rectangles.

*Attributes:*
- `windowManager`: Manages the UI elements on the screen.
- `nodesToDraw`: Set of clickable UI elements.
- `newRectangleViews`: List of new rectangles to be drawn.
- `rectangleViews`: List of existing rectangles.
- `processingIndicator`: Progress bar indicating processing.
- `handler`: Handler for executing tasks on the main thread.
*Methods:*
- `onAccessibilityEvent(event: AccessibilityEvent?)`: Processes accessibility events.
- `onInterrupt()`: Handles service interruption.
- `onServiceConnected()`: Configures the accessibility service.
- `traverseAccessibilityTree(nodeInfo: AccessibilityNodeInfo, depth: Int)`: Traverses the UI tree.
- `addNewRectangles()`: Adds new rectangles for clickable elements.
- `drawRectangles()`: Draws rectangles on the screen
- `drawRectangle(nodeInfo: AccessibilityNodeInfo, color: Int, targetList: MutableList<View>)`: Draws a rectangle around a UI element.
- `clearPreviousRectangles()`: Removes existing rectangles from the screen.
- `showProcessingIndicator()`: Displays the processing indicator.
- `hideProcessingIndicator()`: Hides the processing indicator.

**Output**

https://github.com/Anuhya27/Clickable-and-non-clickable-elements-on-any-screen/assets/56588616/3950d974-c4fd-4abb-973c-b670a542ebf6

