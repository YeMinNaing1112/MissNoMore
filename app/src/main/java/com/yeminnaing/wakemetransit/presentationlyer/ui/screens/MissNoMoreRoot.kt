package com.yeminnaing.wakemetransit.presentationlyer.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.yeminnaing.wakemetransit.R
import com.yeminnaing.wakemetransit.presentationlyer.navigations.MissNoMoreNavGraph

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MissNoMoreRoot(
    initialLat: Double?,
    initialLon: Double?,
) {
    val context = LocalContext.current

    val permissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )

    var permissionChecked by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        val alreadyGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (alreadyGranted) {
            permissionChecked = true
        } else {
            permissionState.launchPermissionRequest()
        }
    }

    LaunchedEffect(permissionState.status) {
        if (permissionState.status.isGranted) {
            permissionChecked = true
        }
    }

    if (!permissionChecked) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = "Miss NO More"
            )
        }

    } else {
        MissNoMoreNavGraph(
            startLat = initialLat,
            startLon = initialLon
        )
    }
}


@Preview
@Composable
private fun MissNoMoreRootPrev() {
    MissNoMoreRoot(initialLat = 223.4, initialLon = 234.4)
}