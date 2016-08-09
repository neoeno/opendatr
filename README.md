# OpenDatr

This is a tool to convert random datasets you find on the internet into sensible formats you might use.

For example, Excel files to CSV, zip files of zip files of shapefiles to GeoJSON, etc.

It's in development. To use it, do this:

```bash
sbt "run path/to/file"
```

It won't do much yet though.

## Roadmap

* Make XLS/XLSX attribute extractor smarter
* Shapefile resolver
* Zip resolver
* Attribute prompting
* Dataset gatherer
* Recipe saver
* Failed dataset reporting

## License

It's all mine. Ask me about it though, I just haven't decided distribution yet