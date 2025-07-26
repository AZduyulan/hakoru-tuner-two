// plugin.js

const { withAndroidManifest, withPlugins } = require('@expo/config-plugins');

const withRecordAudioPermission = (config) => {
  return withAndroidManifest(config, async (config) => {
    const manifest = config.modResults;
    const permissions = manifest.manifest['uses-permission'] || [];

    const hasPermission = permissions.some(
      (p) => p['$']['android:name'] === 'android.permission.RECORD_AUDIO'
    );

    if (!hasPermission) {
      permissions.push({
        $: { 'android:name': 'android.permission.RECORD_AUDIO' },
      });
    }

    manifest.manifest['uses-permission'] = permissions;
    return config;
  });
};

module.exports = function withHakoruTunerTwo(config) {
  return withPlugins(config, [withRecordAudioPermission]);
};
