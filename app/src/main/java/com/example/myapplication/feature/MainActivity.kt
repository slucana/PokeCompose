package com.example.myapplication.feature

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.core.ScreenState
import com.example.myapplication.data.model.Pokemon
import com.example.myapplication.data.model.PokemonMove
import com.example.myapplication.data.model.PokemonType
import com.example.myapplication.ui.*
import com.example.myapplication.ui.theme.typography
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.imageloading.ImageLoadState
import com.google.accompanist.imageloading.LoadPainter
import com.google.accompanist.imageloading.isFinalState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {
                MainScreen(viewModel)
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val screenState: ScreenState<Pokemon>? by viewModel.screenState.observeAsState()
    val pokemonName: String? by viewModel.name.observeAsState()
    val pokemonColor: Color? by viewModel.color.observeAsState()
    val isSearching: Boolean by viewModel.isSearching.observeAsState(true)
    val imageLoaded: Boolean by viewModel.imageLoaded.observeAsState(false)
    val focusManager: FocusManager = LocalFocusManager.current

    Surface(color = MaterialTheme.colors.background) {
        // A surface container using the 'background' color from the theme
        Column(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = if (screenState?.data != null) {
                    Alignment.TopCenter
                } else {
                    Alignment.Center
                }
            ) {
                when {
                    screenState?.data != null ->{
                        PokemonContent(screenState?.data, pokemonColor, imageLoaded, viewModel)
                    }
                    screenState?.error != null -> Error("Oeps!")
                    screenState?.isLoading == true -> Loader()
                    else -> {
                        Image(
                            modifier = Modifier
                                .fillMaxWidth(fraction = 0.6f)
                                .fillMaxHeight(fraction = 0.6f),
                            painter = painterResource(id = R.drawable.ic_pokeball),
                            contentDescription = stringResource(R.string.pokeball)
                        )
                    }
                }
            }
            SearchBox(pokemonName, viewModel, isSearching, focusManager)
        }
    }
}

@Composable
private fun SearchBox(
    pokemonName: String?,
    viewModel: MainViewModel,
    isSearching: Boolean,
    focusManager: FocusManager
) {
    Divider(thickness = 1.dp, color = divider)

    if (isSearching) {
        OutlinedTextField(
            modifier = Modifier
                .padding(Dimens.medium)
                .wrapContentHeight()
                .fillMaxWidth(),
            value = pokemonName.orEmpty(),
            onValueChange = { viewModel.onNameInput(it) },
            singleLine = true,
            label = { Text(stringResource(R.string.pokemon_name_label)) },
            keyboardActions = KeyboardActions(
                onSearch = {
                    viewModel.retrievePokemonDetails(pokemonName.orEmpty())
                    focusManager.clearFocus()
                    viewModel.notifyImageLoaded(false)
                }
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
        )
    } else {
        IconButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { viewModel.toggleSearchState() }
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = stringResource(R.string.expand)
            )
        }
    }
}

@Composable
fun PokemonContent(
    data: Pokemon?,
    pokemonColor: Color?,
    imageLoaded: Boolean,
    viewModel: MainViewModel
) {
    Column(
        modifier = Modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val imagePainter = rememberCoilPainter(
            request = data?.sprites?.url,
            fadeIn = true
        )

        LaunchedEffect(imagePainter) {
            snapshotFlow { imagePainter.loadState }
                .filter { it.isFinalState() }
                .collect {
                    if (it is ImageLoadState.Success) {
                        viewModel.notifyImageLoaded(true)
                        viewModel.toggleSearchState()
                    }
                }
        }

        PokemonHeader(
            baseExperience = data?.baseExperience.toString(),
            imageLoaded,
            pokemonColor ?: primaryColor,
            imagePainter
        )
        LazyColumn(contentPadding = PaddingValues(top = Dimens.medium)) {
            item {
                Carousel(
                    title = stringResource(R.string.types),
                    visible = imageLoaded,
                    listItems = data?.types.orEmpty()
                ) {
                    Type(it)
                }
                CollapsableContent(
                    title = stringResource(R.string.stats),
                    color = pokemonColor ?: MaterialTheme.colors.primary,
                    visible = imageLoaded,
                    startExpanded = true
                ) {
                    Stats(data)
                }
                Carousel(
                    title = stringResource(R.string.moves),
                    visible = imageLoaded,
                    listItems = data?.getMoves().orEmpty()
                ) {
                    Move(pokemonColor, it)
                }
            }
        }
    }
}

@Composable
private fun Stats(data: Pokemon?) {
    Column(
        modifier = Modifier.background(color = lightDarkBackground),
        verticalArrangement = Arrangement.spacedBy(Dimens.smallMedium)
    ) {
        Spacer(Modifier.fillMaxWidth().height(Dimens.smallMedium))
        data?.stats.orEmpty().forEach { stat ->
            Row(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = Dimens.smallMedium),
                    text = stat.stat.name.capitalize(Locale.getDefault()),
                    style = typography.body2
                )
                Text(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(end = Dimens.smallMedium),
                    text = stat.baseStat.toString(),
                    style = typography.body2.copy(fontWeight = FontWeight.ExtraBold)
                )
            }
        }
        Spacer(Modifier.fillMaxWidth().height(Dimens.smallMedium))
    }
}

@Composable
fun Move(pokemonColor: Color?, move: PokemonMove) {
    Card(
        modifier = Modifier
            .wrapContentHeight()
            .wrapContentWidth()
            .clip(RoundedCornerShape(Dimens.small)),
        backgroundColor = pokemonColor ?: primaryColor
    ) {
        Text(
            modifier = Modifier
                .wrapContentWidth()
                .padding(
                    start = Dimens.smallMedium,
                    end = Dimens.smallMedium,
                    top = Dimens.small,
                    bottom = Dimens.small
                ),
            style = typography.body2.copy(color = MaterialTheme.colors.onPrimary),
            text = move.move.name.capitalize(Locale.getDefault())
        )
    }
}

@Composable
fun Type(type: PokemonType) {
    Box(
        modifier = Modifier.wrapContentWidth().wrapContentHeight(),
        contentAlignment = Alignment.CenterStart
    ) {
        Card(
            modifier = Modifier
                .wrapContentWidth()
                .padding(start = Dimens.smallMedium)
                .clip(RoundedCornerShape(Dimens.smallMedium))
                .border(
                    color = type.color,
                    width = 1.dp,
                    shape = RoundedCornerShape(Dimens.smallMedium)
                )
        ) {
            Text(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(
                        start = Dimens.large,
                        end = Dimens.large,
                        top = Dimens.small,
                        bottom = Dimens.small
                    ),
                style = typography.body2.copy(color = MaterialTheme.colors.onBackground),
                text = type.type.name.capitalize(Locale.getDefault())
            )
        }
        Canvas(modifier = Modifier.size(Dimens.iconSize)) {
            drawCircle(color = type.color)
        }
    }
}

@Composable
fun PokemonHeader(
    baseExperience: String,
    imageLoaded: Boolean,
    pokemonColor: Color,
    imagePainter: LoadPainter<Any>
){
    Box(
        modifier = Modifier
            .alpha(if (imageLoaded) 1f else 0f)
            .wrapContentHeight()
            .fillMaxWidth()
            .background(color = pokemonColor),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .padding(Dimens.medium)
                .background(color = background, shape = CircleShape)
        ) {
            Image(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(Dimens.smallMedium),
                painter = imagePainter,
                contentDescription = stringResource(R.string.pokemon_image)
            )
        }
        Text(
            modifier = Modifier.fillMaxWidth().padding(top= Dimens.medium, end = Dimens.medium),
            text = stringResource(R.string.experience, baseExperience),
            textAlign = TextAlign.End,
            style = typography.h5.copy(color = background, fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun <T> Carousel(
    title: String,
    visible: Boolean,
    listItems: List<T>,
    itemContent: @Composable (T) -> Unit
){
    Column(
        modifier = Modifier
            .alpha(if (visible) 1f else 0f)
            .padding(bottom = Dimens.medium)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimens.smallMedium, start = Dimens.medium),
            maxLines = 1,
            text = title,
            style = typography.subtitle1.copy(fontWeight = FontWeight.Bold)
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = Dimens.medium, vertical = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(Dimens.small)
        ) {
            items(listItems) { item -> itemContent(item) }
        }
    }
}

@Composable
fun CollapsableContent(
    title: String,
    color: Color = MaterialTheme.colors.primary,
    visible: Boolean,
    startExpanded: Boolean = false,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(startExpanded) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = Dimens.medium, end = Dimens.medium, bottom = Dimens.medium)
            .animateContentSize() // automatically animate size when it changes
            .alpha(if (visible) 1f else 0f)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = Dimens.smallMedium, bottom = Dimens.small),
                    text = title,
                    style = typography.subtitle1.copy(fontWeight = FontWeight.Bold)
                )
                Icon(
                    modifier = Modifier
                        .requiredSize(Dimens.iconSize)
                        .clickable(onClick = { expanded = !expanded }),
                    imageVector = if (expanded) {
                        Icons.Filled.KeyboardArrowUp
                    } else {
                        Icons.Filled.KeyboardArrowDown
                    },
                    contentDescription = stringResource(R.string.expand)
                )
            }
            Divider(thickness = 1.dp, color = color)

            // content of the card depends on the current value of expanded
            if (expanded) {
                content()
            }
        }
    }
}

@Composable
fun Loader() {
    CircularProgressIndicator()
}

@Composable
fun Error(errorText: String) {
    Text(
        modifier = Modifier.wrapContentWidth(),
        style = typography.h6.copy(color = Color.Red),
        text = errorText
    )
}

@Preview
fun CreateError() {
    com.example.myapplication.feature.Error(errorText = a)
}
