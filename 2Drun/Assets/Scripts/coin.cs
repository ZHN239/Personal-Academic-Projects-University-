using UnityEngine;

public class coin : MonoBehaviour
{
    // Start is called once before the first execution of Update after the MonoBehaviour is created


    private void OnTriggerEnter2D(Collider2D other)
    {
        AudioManager.instance.play("½ð±Ò");
        Destroy(gameObject);
    }
}
