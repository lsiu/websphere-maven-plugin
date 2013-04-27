File stubsDir = new File( basedir, "target/generated-sources/stubs" );

assert stubsDir.isDirectory()

File appendableFile = new File(stubsDir, "java/lang/_Appendable_Stub.class");
File readableFile = new File(stubsDir, "java/lang/_Readable_Stub.class");

assert appendableFile.exists()
assert appendableFile.isFile()
assert readableFile.exists()
assert readableFile.isFile()

