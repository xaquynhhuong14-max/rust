use jni::objects::{JClass, JString};
use jni::sys::jstring;
use jni::JNIEnv;

#[no_mangle]
pub extern "system" fn Java_com_example_filemanager_MainActivity_formatSize(
    mut env: JNIEnv,
    _class: JClass,
    bytes: jni::sys::jlong,
) -> jstring {
    let size = bytes.max(0) as f64;
    let (value, unit) = if size < 1024.0 {
        (size, "B")
    } else if size < 1024.0_f64.powi(2) {
        (size / 1024.0, "KB")
    } else if size < 1024.0_f64.powi(3) {
        (size / 1024.0_f64.powi(2), "MB")
    } else {
        (size / 1024.0_f64.powi(3), "GB")
    };
    let output = if unit == "B" {
        format!("{:.0} {}", value, unit)
    } else {
        format!("{:.1} {}", value, unit)
    };
    env.new_string(output)
        .map(|s| s.into_raw())
        .unwrap_or(std::ptr::null_mut())
         }
