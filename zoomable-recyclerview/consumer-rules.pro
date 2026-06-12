# Consumer ProGuard/R8 rules applied to apps that depend on this library.
# https://developer.android.com/studio/build/shrink-code

# Keep the view and its constructors so it can be inflated from XML when the
# consuming app enables R8/minification. Public Kotlin properties accessed from
# code are kept automatically based on usage.
-keep class com.phonepe.zoomablerecyclerview.ZoomableRecyclerView {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
