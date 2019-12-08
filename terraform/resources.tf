    resource "google_compute_instance" "default" {
      name         = "batchjob-compute-1"
      machine_type = "g1-small"
      zone         = "europe-north1-a"
     
      boot_disk {
        initialize_params {
          image = "ubuntu-os-cloud/ubuntu-1804-lts"
        }
      }
     
      metadata_startup_script = "${file("init.sh")}"
     
      network_interface {
        network = "default"
     
        access_config {
          // Include this section to give the VM an external ip address
        }
      }
     
      tags = ["http-server"]
    }
     
    resource "google_compute_firewall" "http-server" {
      name    = "default-allow-http"
      network = "default"
     
      allow {
        protocol = "tcp"
        ports    = ["80"]
      }
     
      source_ranges = ["0.0.0.0/0"]
      target_tags   = ["http-server"]
    }
     
    output "ip" {
      value = "${google_compute_instance.default.network_interface.0.access_config.0.nat_ip}"
    }