import { useState } from "react";
import axios from "axios";
import {
  Box,
  CircularProgress,
  Container,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  TextField,
  Typography,
  Button,
  colors,
  css,
} from "@mui/material";
import "./App.css";
import { blue } from "@mui/material/colors";

function App() {
  const [emailContent, setEmailContent] = useState("");
  const [tone, setTone] = useState("");
  const [generateReply, setGeneratedReply] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const[buttonText, setButtonText]=useState("Copy to Clipboard")
  const handleSubmit = async () => {
    setLoading(true);
    setError("");
    setButtonText("Copy to Clipboard");
    try {
      const response = await axios.post("http://localhost:8080/api/email-reply/generate", {
        emailContent,
        tone,
      });
      setGeneratedReply(
        typeof response.data === "string" ? response.data : JSON.stringify(response.data)
      );
    } catch (error) {
      setError(error.response?.data?.message || error.message || "An unknown error occurred");
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Typography sx={{ mb: 3 }} variant="h3" component="h1" gutterBottom>
        Auto Email Reply System
      </Typography>
      <Box sx={{ max: 3 }}>
        <TextField
          fullWidth
          multiline
          rows={6}
          variant="outlined"
          label="Original Content"
          value={emailContent || ""}
          onChange={(e) => setEmailContent(e.target.value)}
          sx={{ mb: 2 }}
        />
        <FormControl fullWidth sx={{ mb: 3 }}>
          <InputLabel>Tone(Optional)</InputLabel>
          <Select
            value={tone || ""}
            label={"Tone(Optional)"}
            onChange={(e) => setTone(e.target.value)}
          >
            <MenuItem value="none">None</MenuItem>
            <MenuItem value="professional">Professional</MenuItem>
            <MenuItem value="casual">Casual</MenuItem>
            <MenuItem value="friendly">Friendly</MenuItem>
          </Select>
        </FormControl>
        <Button
          variant="contained"
          onClick={handleSubmit}
          disabled={!emailContent || loading}
          fullWidth
        >
          {loading ? <CircularProgress size={24} /> : "Generate Reply"}
        </Button>
      </Box>
      {error && (
        <Typography color="error" sx={{ mb: 4 }}>
          {error}
        </Typography>
      )}
      {generateReply && (
        <Box sx={{ mt: 3 }}>
          <Typography variant="h6" gutterBottom>
            Generated Reply:
          </Typography>
          <TextField
            fullWidth
            multiline
            minRows={6}
            variant="outlined"
            value={generateReply || ""}
            inputProps={{ readOnly: true }}
          />
          <Button
            variant="outlined"
            
            sx={{ mt: 2 }}
            onClick={() => {navigator.clipboard.writeText(generateReply); setButtonText("textCopied") }}
          >
            {buttonText}
          </Button>
        </Box>
      )}
    </Container>
  );
}

export default App;
