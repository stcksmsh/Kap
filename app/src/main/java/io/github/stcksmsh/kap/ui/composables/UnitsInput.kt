package io.github.stcksmsh.kap.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.stcksmsh.kap.model.VolumeUnits
import io.github.stcksmsh.kap.model.WeightUnits
import io.github.stcksmsh.kap.ui.theme.AppTypography
import io.github.stcksmsh.kap.R


@Composable
fun UnitsInput(
    selectedWeightUnit: WeightUnits,
    selectedVolumeUnit: VolumeUnits,
    onSelectedWeightUnitChanged: (WeightUnits) -> Unit,
    onSelectedVolumeUnitChanged: (VolumeUnits) -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.units),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.primary,
        )

        WeightUnitDropdown(
            selectedWeightUnit = selectedWeightUnit,
            onUnitSelected = onSelectedWeightUnitChanged,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        VolumeUnitDropdown(
            selectedVolumeUnit = selectedVolumeUnit,
            onUnitSelected = onSelectedVolumeUnitChanged,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VolumeUnitDropdown(
    selectedVolumeUnit: VolumeUnits,
    modifier: Modifier = Modifier,
    onUnitSelected: (VolumeUnits) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded, onExpandedChange = { expanded = !expanded }, modifier = modifier
    ) {
        TextField(
            value = selectedVolumeUnit.label(),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            textStyle = AppTypography.bodyLarge.copy(
            )
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            VolumeUnits.entries.forEach { unit ->
                DropdownMenuItem(text = {
                    Text(
                        text = "${unit.label()} - ${unit.fullName}",
                        style = AppTypography.bodySmall,
                        textAlign = TextAlign.Start,
                    )
                }, onClick = {
                    onUnitSelected(unit)
                    expanded = false
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightUnitDropdown(
    selectedWeightUnit: WeightUnits,
    modifier: Modifier = Modifier,
    onUnitSelected: (WeightUnits) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded, onExpandedChange = { expanded = !expanded }, modifier = modifier
    ) {
        TextField(
            value = selectedWeightUnit.label(),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            textStyle = AppTypography.bodyLarge.copy(
            )
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            WeightUnits.entries.forEach { unit ->
                DropdownMenuItem(text = {
                    Text(
                        text = "${unit.label()} - ${unit.fullName}",
                        style = AppTypography.bodySmall,
                        textAlign = TextAlign.Start,
                    )
                }, onClick = {
                    onUnitSelected(unit)
                    expanded = false
                })
            }
        }
    }
}
