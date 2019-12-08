    provider "google" {
      credentials = "${file("service-key.json")}"
      project     = "mcc-fall-2019-g14"
      region      = "europe-north1"
      zone        = "europe-north1-a"
    }