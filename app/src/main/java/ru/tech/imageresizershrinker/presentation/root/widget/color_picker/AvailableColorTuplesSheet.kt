package ru.tech.imageresizershrinker.presentation.root.widget.color_picker

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import com.t8rin.dynamic.theme.ColorTuple
import com.t8rin.dynamic.theme.ColorTupleItem
import com.t8rin.dynamic.theme.PaletteStyle
import com.t8rin.dynamic.theme.rememberColorScheme
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.icons.material.CreateAlt
import ru.tech.imageresizershrinker.presentation.root.icons.material.PaletteSwatch
import ru.tech.imageresizershrinker.presentation.root.shapes.DavidStarShape
import ru.tech.imageresizershrinker.presentation.root.theme.defaultColorTuple
import ru.tech.imageresizershrinker.presentation.root.theme.inverse
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.helper.ListUtils.nearestFor
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedButton
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedSliderItem
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.alertDialogBorder
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.container
import ru.tech.imageresizershrinker.presentation.root.widget.palette_selection.PaletteStyleSelection
import ru.tech.imageresizershrinker.presentation.root.widget.sheets.SimpleDragHandle
import ru.tech.imageresizershrinker.presentation.root.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.presentation.root.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.presentation.root.widget.text.TitleItem
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalWindowSizeClass

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun AvailableColorTuplesSheet(
    visible: MutableState<Boolean>,
    colorTupleList: List<ColorTuple>,
    currentColorTuple: ColorTuple,
    openColorPicker: () -> Unit,
    colorPicker: @Composable (onUpdateColorTuples: (List<ColorTuple>) -> Unit) -> Unit,
    onPickTheme: (ColorTuple) -> Unit,
    updateThemeContrast: (Float) -> Unit,
    onThemeStyleSelected: (PaletteStyle) -> Unit,
    onUpdateColorTuples: (List<ColorTuple>) -> Unit,
) {
    val showEditColorPicker = rememberSaveable { mutableStateOf(false) }

    val settingsState = LocalSettingsState.current
    SimpleSheet(
        visible = visible,
        endConfirmButtonPadding = 0.dp,
        dragHandle = {
            SimpleDragHandle {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    TitleItem(
                        text = stringResource(R.string.color_scheme),
                        icon = Icons.Rounded.PaletteSwatch
                    )
                }
            }
        },
        title = {
            var showConfirmDeleteDialog by remember { mutableStateOf(false) }

            if (showConfirmDeleteDialog) {
                AlertDialog(
                    modifier = Modifier.alertDialogBorder(),
                    onDismissRequest = { showConfirmDeleteDialog = false },
                    confirmButton = {
                        EnhancedButton(
                            onClick = { showConfirmDeleteDialog = false }
                        ) {
                            Text(stringResource(R.string.cancel))
                        }
                    },
                    dismissButton = {
                        EnhancedButton(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            onClick = {
                                showConfirmDeleteDialog = false
                                if ((colorTupleList - currentColorTuple).isEmpty()) {
                                    onPickTheme(defaultColorTuple)
                                } else {
                                    colorTupleList.nearestFor(currentColorTuple)
                                        ?.let { onPickTheme(it) }
                                }
                                onUpdateColorTuples(colorTupleList - currentColorTuple)
                            }
                        ) {
                            Text(stringResource(R.string.delete))
                        }
                    },
                    title = {
                        Text(stringResource(R.string.delete_color_scheme_title))
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = null
                        )
                    },
                    text = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            ColorTupleItem(
                                colorTuple = currentColorTuple,
                                modifier = Modifier
                                    .padding(2.dp)
                                    .size(64.dp)
                                    .container(
                                        shape = DavidStarShape,
                                        color = rememberColorScheme(
                                            isDarkTheme = settingsState.isNightMode,
                                            amoledMode = settingsState.isDynamicColors,
                                            colorTuple = currentColorTuple,
                                            contrastLevel = settingsState.themeContrastLevel,
                                            style = settingsState.themeStyle,
                                            dynamicColor = settingsState.isDynamicColors
                                        ).surfaceVariant.copy(alpha = 0.8f),
                                        borderColor = MaterialTheme.colorScheme.outlineVariant(0.2f),
                                        resultPadding = 0.dp
                                    )
                                    .padding(3.dp),
                                backgroundColor = Color.Transparent
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(stringResource(R.string.delete_color_scheme_warn))
                        }
                    }
                )
            }
            Row {
                EnhancedButton(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    onClick = {
                        showConfirmDeleteDialog = true
                    },
                    borderColor = MaterialTheme.colorScheme.outlineVariant(
                        onTopOf = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Icon(Icons.Rounded.Delete, null)
                }
                Spacer(Modifier.width(8.dp))
                EnhancedButton(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = {
                        showEditColorPicker.value = true
                    }
                ) {
                    Icon(Icons.Rounded.CreateAlt, null)
                }
            }
        },
        sheetContent = {
            val portrait =
                LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE || LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Compact

            val palette = @Composable {
                PaletteStyleSelection(
                    onThemeStyleSelected = onThemeStyleSelected,
                    shape = RoundedCornerShape(
                        topStart = 24.dp,
                        topEnd = 24.dp,
                        bottomStart = 4.dp,
                        bottomEnd = 4.dp
                    )
                )
            }
            val slider = @Composable {
                var state by remember(settingsState.themeContrastLevel) {
                    mutableFloatStateOf(settingsState.themeContrastLevel.toFloat())
                }
                EnhancedSliderItem(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    value = state,
                    title = stringResource(id = R.string.contrast),
                    valueRange = -1f..1f,
                    shape = RoundedCornerShape(
                        topStart = 4.dp,
                        topEnd = 4.dp,
                        bottomStart = 24.dp,
                        bottomEnd = 24.dp
                    ),
                    onValueChange = { state = it.roundToTwoDigits() },
                    steps = 198,
                    onValueChangeFinished = {
                        updateThemeContrast(state)
                    },
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!portrait) {
                    Column(
                        Modifier
                            .verticalScroll(rememberScrollState())
                            .weight(0.8f)
                            .padding(16.dp)
                    ) {
                        palette()
                        Spacer(Modifier.height(4.dp))
                        slider()
                    }
                }
                LazyVerticalGrid(
                    modifier = Modifier.weight(1f),
                    columns = GridCells.Adaptive(64.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        4.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
                ) {
                    if (portrait) {
                        item(
                            span = { GridItemSpan(maxLineSpan) }
                        ) {
                            palette()
                        }
                        item(
                            span = { GridItemSpan(maxLineSpan) }
                        ) {
                            slider()
                        }
                    }
                    items(colorTupleList) { colorTuple ->
                        ColorTupleItem(
                            colorTuple = remember(settingsState.themeStyle, colorTuple) {
                                derivedStateOf {
                                    if (settingsState.themeStyle == PaletteStyle.TonalSpot) {
                                        colorTuple
                                    } else colorTuple.run {
                                        copy(secondary = primary, tertiary = primary)
                                    }
                                }
                            }.value,
                            modifier = Modifier
                                .aspectRatio(1f)
                                .container(
                                    shape = DavidStarShape,
                                    color = rememberColorScheme(
                                        isDarkTheme = settingsState.isNightMode,
                                        amoledMode = LocalSettingsState.current.isDynamicColors,
                                        colorTuple = colorTuple,
                                        contrastLevel = settingsState.themeContrastLevel,
                                        style = settingsState.themeStyle,
                                        dynamicColor = settingsState.isDynamicColors
                                    ).surfaceVariant.copy(alpha = 0.8f),
                                    borderColor = MaterialTheme.colorScheme.outlineVariant(0.2f),
                                    resultPadding = 0.dp
                                )
                                .combinedClickable(
                                    onClick = {
                                        onPickTheme(colorTuple)
                                    },
                                )
                                .padding(3.dp),
                            backgroundColor = Color.Transparent
                        ) {
                            AnimatedContent(
                                targetState = colorTuple == currentColorTuple
                            ) { selected ->
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    if (selected) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .background(
                                                    animateColorAsState(
                                                        colorTuple.primary.inverse(
                                                            fraction = { cond ->
                                                                if (cond) 0.8f
                                                                else 0.5f
                                                            },
                                                            darkMode = colorTuple.primary.luminance() < 0.3f
                                                        )
                                                    ).value,
                                                    CircleShape
                                                ),
                                        )
                                        Icon(
                                            imageVector = Icons.Rounded.Done,
                                            contentDescription = null,
                                            tint = colorTuple.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    item {
                        ColorTupleItem(
                            colorTuple = ColorTuple(
                                primary = MaterialTheme.colorScheme.secondary,
                                secondary = MaterialTheme.colorScheme.secondary,
                                tertiary = MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier
                                .aspectRatio(1f)
                                .container(
                                    shape = DavidStarShape,
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    borderColor = MaterialTheme.colorScheme.outlineVariant(0.2f),
                                    resultPadding = 0.dp
                                )
                                .clickable { openColorPicker() }
                                .padding(3.dp),
                            backgroundColor = Color.Transparent
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AddCircleOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    visible.value = false
                }
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        },
    )
    ColorTuplePicker(
        visible = showEditColorPicker,
        colorTuple = currentColorTuple,
        onColorChange = {
            onUpdateColorTuples(colorTupleList + it - currentColorTuple)
            onPickTheme(it)
        }
    )
    colorPicker(onUpdateColorTuples)
}