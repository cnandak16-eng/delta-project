package com.example.deltaproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import java.nio.file.WatchEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.fillMaxSize

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val photos = listOf(

            Photo(
                id = 1,
                imageRes = R.drawable.scenery

            ),

            Photo(
                id = 2,
                imageRes = R.drawable.ic_launcher_background
            ),

            Photo(
                id = 3,
                imageRes = R.drawable.desert
            ),

            Photo(
                id = 4,
                imageRes = R.drawable.factory
            ),
            Photo(
                id=5,
                imageRes = R.drawable.nature1
            )
        )

        setContent {

            ImageGrid(
                photos = photos
            )
        }
    }
}

data class Photo(
    val id: Int,
    val imageRes: Int,
    val transformState: TransformState= TransformState()

)
class TransformState {

    var scale by mutableStateOf(1f)

    var rotation by mutableStateOf(0f)

    var offsetX by mutableStateOf(0f)

    var offSetY by mutableStateOf(0f)
}
@Composable
fun ImageGrid(photos: List<Photo>) {
    var spacing by remember { mutableStateOf(8f) }
    var radius by remember { mutableStateOf(60f) }
    var selectedLayout by remember { mutableStateOf(2) }
    val collagePhoto = remember {

        mutableStateListOf(
            photos[0],
            photos[1],
            photos[2],
            photos[3]
        )
    }
    var activeSlot by remember { mutableStateOf<Int?>(null) }
    var nextId by remember { mutableStateOf(1000) }
    val appTextStyle=TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Serif,
        color = Color.Red
    )
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text="SPACING",
            style=appTextStyle)
        Slider(
            value = spacing,
            onValueChange = {spacing=it},
            valueRange = 0f..32f
        )
        Text(text="RADIUS",
            style =appTextStyle)
        Slider(
            value = radius,
            onValueChange = {radius=it},
            valueRange = 0f..60f
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { selectedLayout = 2 }) {
                Text(text="2 Photos",
                    style =appTextStyle.copy(fontSize = 14.sp))
            }
            Button(onClick = { selectedLayout = 3 }) {
                Text(text="3 Photos",
                    style =appTextStyle.copy(fontSize = 14.sp))
            }
            Button(onClick = { selectedLayout = 4 }) {
                Text(text="4 Photos",
                    style =appTextStyle.copy(fontSize = 14.sp))
            }
        }

            Box(modifier = Modifier.weight(1f)) {
                Image(
                    painter = painterResource(R.drawable.neonbg),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                when (selectedLayout) {
                    2 -> {
                        TwoPhotoLayout(
                            photos = collagePhoto,
                            spacing = spacing,
                            radius = radius,
                            activeSlot = activeSlot,
                            onSlotSelected = { activeSlot = it }
                        )
                    }

                    3 -> {
                        ThreePhotoLayout(
                            photos = collagePhoto,
                            spacing = spacing,
                            radius = radius,
                            activeSlot = activeSlot,
                            onSlotSelected = { activeSlot = it }
                        )
                    }

                    4 -> {
                        FourPhotoLayout(
                            photos = collagePhoto,
                            spacing = spacing,
                            radius = radius,
                            activeSlot = activeSlot,
                            onSlotSelected = { activeSlot = it }
                        )
                    }
                }
            }

        LazyRow(modifier = Modifier.fillMaxWidth()) {
            items(photos){photo ->
                Image(
                    painter=painterResource(photo.imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable{
                            if(activeSlot!=null){
                                collagePhoto[activeSlot!!]=Photo(
                                    id=nextId++,
                                    imageRes = photo.imageRes,
                                    transformState = TransformState()
                                )
                            }
                        }
                )
            }
        }
        }
    }


@Composable
fun EditableImage(
    photo: Photo,
    spacing: Float,
    radius: Float,
    modifier: Modifier,
    onClick: () -> Unit,
    isSelected: Boolean
) {

    val transformState = photo.transformState

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(spacing.dp)

            .clip(RoundedCornerShape(radius.dp))

            .border(
                width = if (isSelected) 4.dp else 0.dp,
                color = Color.White,
                shape = RoundedCornerShape(radius.dp)
            )
    ) {

        Image(
            painter = painterResource(photo.imageRes),

            contentDescription = null,

            contentScale = ContentScale.Crop,

            modifier = Modifier

                .matchParentSize()
                .pointerInput(photo.id){
                    detectTapGestures {
                        onClick()
                    }
                }
                .pointerInput(photo.id) {

                    detectTransformGestures { _, pan, zoom, rotate ->

                        onClick()

                        transformState.scale =
                            (transformState.scale * zoom)


                        transformState.rotation += rotate

                        transformState.offsetX += pan.x
                        transformState.offSetY += pan.y
                    }
                }

                .graphicsLayer {

                    scaleX = transformState.scale
                    scaleY = transformState.scale

                    rotationZ = transformState.rotation

                    translationX = transformState.offsetX
                    translationY = transformState.offSetY
                }
        )
    }
}
@Composable
fun TwoPhotoLayout(
    photos: List<Photo>,
    spacing: Float,
    radius: Float,
    modifier: Modifier = Modifier,
    activeSlot: Int?,
    onSlotSelected: (Int) -> Unit
) {

    Row(
        modifier = Modifier.fillMaxSize()
    ) {

        EditableImage(
            photo = photos[0],

            spacing = spacing,

            radius = radius,

            modifier = Modifier
                .weight(1f),


            onClick = { onSlotSelected(0) },

            isSelected = activeSlot == 0
        )

        EditableImage(
            photo = photos[1],

            spacing = spacing,

            radius = radius,

            modifier = Modifier
                .weight(1f),
            onClick = { onSlotSelected(1) },

            isSelected = activeSlot == 1
        )
    }
}
@Composable
fun ThreePhotoLayout(
    photos: List<Photo>,
    spacing: Float,
    radius: Float,
    modifier: Modifier = Modifier,
    activeSlot: Int?,
    onSlotSelected: (Int) -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        EditableImage(
            photo = photos[0],

            spacing = spacing,

            radius = radius,

            modifier = Modifier
                .weight(1f),

            onClick = { onSlotSelected(0) },

            isSelected = activeSlot == 0
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {

            EditableImage(
                photo = photos[1],

                spacing = spacing,

                radius = radius,

                modifier = Modifier
                    .weight(1f),

                onClick = { onSlotSelected(1) },

                isSelected = activeSlot == 1
            )

            EditableImage(
                photo = photos[2],

                spacing = spacing,

                radius = radius,

                modifier = Modifier
                    .weight(1f),

                onClick = { onSlotSelected(2) },

                isSelected = activeSlot == 2
            )
        }
    }
}
@Composable
fun FourPhotoLayout(
    photos: List<Photo>,
    spacing: Float,
    radius: Float,
    modifier: Modifier = Modifier,
    activeSlot: Int?,
    onSlotSelected: (Int) -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {

            EditableImage(
                photo = photos[0],

                spacing = spacing,

                radius = radius,

                modifier = Modifier
                    .weight(1f),

                onClick = { onSlotSelected(0) },

                isSelected = activeSlot == 0
            )

            EditableImage(
                photo = photos[1],

                spacing = spacing,

                radius = radius,

                modifier = Modifier
                    .weight(1f),

                onClick = { onSlotSelected(1) },

                isSelected = activeSlot == 1
            )
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {

            EditableImage(
                photo = photos[2],

                spacing = spacing,

                radius = radius,

                modifier = Modifier
                    .weight(1f),

                onClick = { onSlotSelected(2) },

                isSelected = activeSlot == 2
            )

            EditableImage(
                photo = photos[3],

                spacing = spacing,

                radius = radius,

                modifier = Modifier
                    .weight(1f),

                onClick = { onSlotSelected(3) },

                isSelected = activeSlot == 3
            )
        }
    }
}