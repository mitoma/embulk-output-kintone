Embulk::JavaPlugin.register_output(
  "kintone", "org.embulk.output.KintoneOutputPlugin",
  File.expand_path('../../../../classpath', __FILE__))
