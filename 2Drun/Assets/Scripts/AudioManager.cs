using UnityEngine;

public class AudioManager : MonoBehaviour
{
    // Start is called once before the first execution of Update after the MonoBehaviour is created
    public static AudioManager instance;
    private AudioSource player;
    void Start()
    {
        instance = this;
        player= GetComponent<AudioSource>();    
    }


    public void play(string name)
    {
        AudioClip clip = Resources.Load<AudioClip>(name);
        player.PlayOneShot(clip);
    }

    // Update is called once per frame
    void Update()
    {
        
    }
}
